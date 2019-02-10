package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.ErrorListener;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.map.GameMap;
import ch.logixisland.anuto.engine.logic.map.PlateauInfo;
import ch.logixisland.anuto.engine.logic.map.WaveInfo;
import ch.logixisland.anuto.engine.logic.persistence.GamePersister;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class GameLoader implements ErrorListener {

    private static final String TAG = GameLoader.class.getSimpleName();
    private static final String SAVED_GAME_FILE = "saved_game.json";

    public interface Listener {
        void gameLoaded();
    }

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final GamePersister mGamePersister;
    private final Viewport mViewport;
    private final EntityRegistry mEntityRegistry;
    private final MapRepository mMapRepository;
    private String mCurrentMapId;

    private List<Listener> mListeners = new CopyOnWriteArrayList<>();

    public GameLoader(Context context, GameEngine gameEngine, GamePersister gamePersister,
                      Viewport viewport, EntityRegistry entityRegistry, MapRepository mapRepository) {
        mContext = context;
        mGameEngine = gameEngine;
        mGamePersister = gamePersister;
        mViewport = viewport;
        mEntityRegistry = entityRegistry;
        mMapRepository = mapRepository;

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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    restart();
                }
            });
            return;
        }

        if (mCurrentMapId == null) {
            return;
        }

        loadMap(mCurrentMapId);
    }

    public void saveGame() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    saveGame();
                }
            });
            return;
        }

        Log.i(TAG, "Saving game...");
        KeyValueStore gameState = new KeyValueStore();
        mGamePersister.writeState(gameState);
        gameState.putInt("appVersion", BuildConfig.VERSION_CODE);
        gameState.putString("mapId", mCurrentMapId);

        try {
            FileOutputStream outputStream = mContext.openFileOutput(SAVED_GAME_FILE, Context.MODE_PRIVATE);
            gameState.toStream(outputStream);
            outputStream.close();
            Log.i(TAG, "Game saved.");
        } catch (Exception e) {
            mContext.deleteFile(SAVED_GAME_FILE);
            throw new RuntimeException("Could not save game!", e);
        }
    }

    public void loadGame() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    loadGame();
                }
            });
            return;
        }

        Log.i(TAG, "Loading state...");
        KeyValueStore gameState;

        try {
            FileInputStream inputStream = mContext.openFileInput(SAVED_GAME_FILE);
            gameState = KeyValueStore.fromStream(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No save game file found.");
            loadMap(mMapRepository.getDefaultMapId());
            return;
        } catch (Exception e) {
            throw new RuntimeException("Could not load game!", e);
        }

        if (gameState.getInt("appVersion") != BuildConfig.VERSION_CODE) {
            Log.i(TAG, "App version mismatch.");
            loadMap(mMapRepository.getDefaultMapId());
            return;
        }

        mCurrentMapId = gameState.getString("mapId");
        initializeGame(mCurrentMapId, gameState);
    }

    public void loadMap(final String mapId) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    loadMap(mapId);
                }
            });
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
            mContext.deleteFile(SAVED_GAME_FILE);
        }
    }

}
