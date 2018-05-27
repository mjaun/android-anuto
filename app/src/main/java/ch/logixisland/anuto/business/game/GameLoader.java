package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import org.simpleframework.xml.Serializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.GameDescriptor;
import ch.logixisland.anuto.data.SerializerFactory;
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

    public interface Listener {
        void gameLoaded();
    }

    private final Serializer mSerializer;
    private final Context mContext;
    private final GameEngine mGameEngine;
    private final GamePersister mGamePersister;
    private final Viewport mViewport;
    private final EntityRegistry mEntityRegistry;
    private final MapRepository mMapRepository;

    private GameDescriptor mGameDescriptor;

    private List<Listener> mListeners = new CopyOnWriteArrayList<>();

    public GameLoader(Context context, GameEngine gameEngine, GamePersister gamePersister,
                      Viewport viewport, EntityRegistry entityRegistry, MapRepository mapRepository) {
        mSerializer = SerializerFactory.createSerializer();
        mContext = context;
        mGameEngine = gameEngine;
        mGamePersister = gamePersister;
        mViewport = viewport;
        mEntityRegistry = entityRegistry;
        mMapRepository = mapRepository;
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
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
            gameDescriptor = GameDescriptor.fromXml(
                    mSerializer,
                    mContext.getResources(),
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

        Log.i(TAG, "Loading game...");
        GameDescriptor gameDescriptor = null;

        try {

            FileInputStream inputStream = mContext.openFileInput(SAVED_GAME_FILE);
            gameDescriptor = mSerializer.read(GameDescriptor.class, inputStream);
            inputStream.close();

            if (gameDescriptor.getAppVersion() != BuildConfig.VERSION_CODE) {
                Log.i(TAG, "Ignoring saved game: version does not match.");
                gameDescriptor = null;
            }

            Log.i(TAG, "Game loaded.");
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No save game file found.");
        } catch (Exception e) {
            mContext.deleteFile(SAVED_GAME_FILE);
            throw new RuntimeException("Could not load game!", e);
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

        Log.i(TAG, "Saving game...");
        mGameDescriptor.clearEntityDescriptors();
        mGameDescriptor.clearActiveWaveDescriptors();
        mGamePersister.writeDescriptor(mGameDescriptor);

        try {
            FileOutputStream outputStream = mContext.openFileOutput(SAVED_GAME_FILE, Context.MODE_PRIVATE);
            mSerializer.write(mGameDescriptor, outputStream);
            outputStream.close();

            Log.i(TAG, "Game saved.");
        } catch (Exception e) {
            mContext.deleteFile(SAVED_GAME_FILE);
            Log.e(TAG, "Could not save game!", e);
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

        for (Listener listener : mListeners) {
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
