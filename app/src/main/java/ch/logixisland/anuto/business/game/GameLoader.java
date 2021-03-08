package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.ErrorListener;
import ch.logixisland.anuto.engine.logic.map.GameMap;
import ch.logixisland.anuto.engine.logic.map.PlateauInfo;
import ch.logixisland.anuto.engine.logic.map.WaveInfo;
import ch.logixisland.anuto.engine.logic.persistence.GamePersister;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class GameLoader implements ErrorListener {

    private static final String TAG = GameLoader.class.getSimpleName();

    public interface Listener {
        void gameLoaded();
    }

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final GamePersister mGamePersister;
    private final Viewport mViewport;
    private final EntityRegistry mEntityRegistry;
    private final MapRepository mMapRepository;
    private final SaveGameRepository mSaveGameRepository;
    private String mCurrentMapId;

    private final SaveGameMigrator mSaveGameMigrator = new SaveGameMigrator();
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    public GameLoader(Context context, GameEngine gameEngine, GamePersister gamePersister,
                      Viewport viewport, EntityRegistry entityRegistry, MapRepository mapRepository,
                      SaveGameRepository saveGameRepository) {
        mContext = context;
        mGameEngine = gameEngine;
        mGamePersister = gamePersister;
        mViewport = viewport;
        mEntityRegistry = entityRegistry;
        mMapRepository = mapRepository;
        mSaveGameRepository = saveGameRepository;

        mGameEngine.registerErrorListener(this);
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    public String getCurrentMapId() {
        return mCurrentMapId;
    }

    public void restart() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(this::restart);
            return;
        }

        if (mCurrentMapId == null) {
            return;
        }

        loadMap(mCurrentMapId);
    }

    public void autoLoadGame() {
        File autoSaveStateFile = mSaveGameRepository.getAutoSaveStateFile();

        if (autoSaveStateFile.exists()) {
            loadGame(autoSaveStateFile);
        } else {
            Log.i(TAG, "No auto save game file not found.");
            loadMap(mMapRepository.getDefaultMapId());
        }
    }

    public void loadGame(final File stateFile) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> loadGame(stateFile));
            return;
        }

        Log.i(TAG, "Loading game...");
        KeyValueStore gameState;

        try {
            FileInputStream inputStream = new FileInputStream(stateFile);
            gameState = KeyValueStore.fromStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Could not load game!", e);
        }

        if (!mSaveGameMigrator.migrate(gameState)) {
            Log.w(TAG, "Failed to migrate save game!");
            loadMap(mMapRepository.getDefaultMapId());
            return;
        }

        mCurrentMapId = gameState.getString("mapId");
        initializeGame(mCurrentMapId, gameState);
    }

    public void loadMap(final String mapId) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> loadMap(mapId));
            return;
        }

        mCurrentMapId = mapId;
        initializeGame(mCurrentMapId, null);
    }

    private void initializeGame(String mapId, KeyValueStore gameState) {
        Log.d(TAG, "Initializing game...");
        mGameEngine.clear();

        MapInfo mapInfo = mMapRepository.getMapById(mapId);
        GameMap map = new GameMap(KeyValueStore.fromResources(mContext.getResources(), mapInfo.getMapDataResId()));
        mGameEngine.setGameMap(map);

        KeyValueStore waveData = KeyValueStore.fromResources(mContext.getResources(), R.raw.waves);
        List<WaveInfo> waveInfos = new ArrayList<>();
        for (KeyValueStore data : waveData.getStoreList("waves")) {
            waveInfos.add(new WaveInfo(data));
        }
        mGameEngine.setWaveInfos(waveInfos);

        mViewport.setGameSize(map.getWidth(), map.getHeight());

        if (gameState != null) {
            mGamePersister.readState(gameState);
        } else {
            mGamePersister.resetState();
            initializeMap(map);
        }

        for (Listener listener : mListeners) {
            listener.gameLoaded();
        }

        Log.d(TAG, "Game loaded.");
    }

    private void initializeMap(GameMap map) {
        for (PlateauInfo info : map.getPlateaus()) {
            Plateau plateau = (Plateau) mEntityRegistry.createEntity(info.getName());
            plateau.setPosition(info.getPosition());
            mGameEngine.add(plateau);
        }
    }

    @Override
    public void error(Exception e, int loopCount) {
        // avoid game not starting anymore because of a somehow corrupt saved game file
        if (loopCount < 10) {
            Log.w(TAG, "Game crashed just after loading, deleting saved game file.");

            //noinspection ResultOfMethodCallIgnored
            mSaveGameRepository.getAutoSaveStateFile().delete();
        }
    }

}
