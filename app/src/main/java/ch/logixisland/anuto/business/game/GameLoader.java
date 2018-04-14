package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.GameDescriptor;
import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.map.PlateauDescriptor;
import ch.logixisland.anuto.engine.logic.GameConfiguration;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.persistence.GamePersister;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.plateau.Plateau;

public class GameLoader {

    private static final String TAG = GameLoader.class.getSimpleName();
    private static final String SAVED_GAME_FILE = "saved_game.xml";

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final GamePersister mGamePersister;
    private final Viewport mViewport;
    private final EntityRegistry mEntityRegistry;
    private final MapRepository mMapRepository;

    private GameDescriptor mGameDescriptor;

    private List<GameLoaderListener> mListeners = new CopyOnWriteArrayList<>();

    public GameLoader(Context context, GameEngine gameEngine, GamePersister gamePersister,
                      Viewport viewport, EntityRegistry entityRegistry, MapRepository mapRepository) {
        mContext = context;
        mGameEngine = gameEngine;
        mGamePersister = gamePersister;
        mViewport = viewport;

        mEntityRegistry = entityRegistry;
        mMapRepository = mapRepository;
    }

    public void addListener(GameLoaderListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameLoaderListener listener) {
        mListeners.remove(listener);
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

        if (mGameDescriptor == null) {
            return;
        }

        MapInfo currentMap = mMapRepository.getMapById(mGameDescriptor.getMapId());
        loadMap(currentMap);
    }

    public void loadMap(final MapInfo mapInfo) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    loadMap(mapInfo);
                }
            });
            return;
        }

        GameDescriptor gameDescriptor;

        try {
            gameDescriptor = GameDescriptor.fromXml(mContext,
                    R.raw.game_settings,
                    R.raw.enemy_settings,
                    R.raw.tower_settings,
                    R.raw.wave_descriptors,
                    mapInfo.getMapDescriptorResId(),
                    mapInfo.getMapId());
        } catch (Exception e) {
            throw new RuntimeException("Could not load game!", e);
        }

        loadGame(gameDescriptor, false);
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

        GameDescriptor gameDescriptor = null;

        try {
            FileInputStream inputStream = mContext.openFileInput(SAVED_GAME_FILE);
            gameDescriptor = GameDescriptor.fromXml(inputStream);
            inputStream.close();

            if (gameDescriptor.getAppVersion() != BuildConfig.VERSION_CODE) {
                Log.i(TAG, "Ignoring save game file: version does not match.");
                gameDescriptor = null;
            }
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No save game file found.");
        } catch (Exception e) {
            Log.e(TAG, "Loading saved game failed!", e);
        }

        if (gameDescriptor != null) {
            loadGame(gameDescriptor, true);
        } else {
            loadMap(mMapRepository.getDefaultMapInfo());
        }
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

        mGameDescriptor.clearEntityDescriptors();
        mGameDescriptor.clearActiveWaveDescriptors();
        mGamePersister.writeDescriptor(mGameDescriptor);

        try {
            FileOutputStream outputStream = mContext.openFileOutput(SAVED_GAME_FILE, Context.MODE_PRIVATE);
            mGameDescriptor.toXml(outputStream);
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Could not save game!", e);
        }
    }

    private void loadGame(GameDescriptor gameDescriptor, boolean mapInitialized) {
        mGameEngine.clear();

        mGameDescriptor = gameDescriptor;
        MapDescriptor mapDescriptor = mGameDescriptor.getMapDescriptor();
        mGameEngine.setGameConfiguration(new GameConfiguration(mGameDescriptor));
        mViewport.setGameSize(mapDescriptor.getWidth(), mapDescriptor.getHeight());
        mGamePersister.readDescriptor(mGameDescriptor);

        if (!mapInitialized) {
            initializeMap(mapDescriptor);
        }

        for (GameLoaderListener listener : mListeners) {
            listener.gameLoaded();
        }
    }

    private void initializeMap(MapDescriptor mapDescriptor) {
        for (PlateauDescriptor descriptor : mapDescriptor.getPlateaus()) {
            Plateau plateau = (Plateau) mEntityRegistry.createEntity(descriptor.getName());
            plateau.setPosition(descriptor.getPosition());
            mGameEngine.add(plateau);
        }
    }
}
