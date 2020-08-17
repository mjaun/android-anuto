package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public static final String SAVED_GAME_FILE = "saved_game.json";
    public static final String SAVED_SCREENSHOT_FILE = "screen.png";
    public static final String SAVED_GAMEINFO_FILE = "gameinfo.json";


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

    public KeyValueStore readSaveGame(final String fileName, final boolean userSavegame) {
        KeyValueStore gameState;

        try {
            gameState = getGameState(fileName, userSavegame);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No save game file found.");
            return null;
        } catch (Exception e) {
            Log.i(TAG, "Could not read save game!");
            return null;
        }

        if (gameState.getInt("appVersion") != BuildConfig.VERSION_CODE) {
            Log.i(TAG, "App version mismatch.");
            return null;
        }
        return gameState;
    }

    public void loadGame() {
        loadGame(SAVED_GAME_FILE, false);
    }

    public void loadGame(final String fileName, final boolean userSavegame) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    loadGame(fileName, userSavegame);
                }
            });
            return;
        }

        Log.i(TAG, "Loading state...");
        KeyValueStore gameState;

        try {
            gameState = getGameState(fileName, userSavegame);
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

    public KeyValueStore getGameState(final String fileName, final boolean userSavegame) throws IOException {
        Log.i(TAG, "Reading state...");
        KeyValueStore gameState;

        FileInputStream inputStream = null;
        try {
            inputStream = userSavegame ? new FileInputStream(fileName) : mContext.openFileInput(fileName);
            gameState = KeyValueStore.fromStream(inputStream);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        return gameState;
    }

    public void loadGameState(final KeyValueStore gameState) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    loadGameState(gameState);
                }
            });
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
