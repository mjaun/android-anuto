package ch.logixisland.anuto.business.game;

import android.content.Context;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.data.GameDescriptor;
import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.map.PlateauDescriptor;
import ch.logixisland.anuto.engine.logic.GameConfiguration;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.plateau.Plateau;

public class GameLoader implements GameStateListener {

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;
    private final EntityRegistry mEntityRegistry;
    private final GameState mGameState;
    private final MapRepository mMapRepository;

    private MapInfo mMapInfo;

    public GameLoader(Context context, GameEngine gameEngine, ScoreBoard scoreBoard,
                      GameState gameState, Viewport viewport,
                      EntityRegistry entityRegistry, MapRepository mapRepository) {
        mContext = context;
        mGameEngine = gameEngine;
        mViewport = viewport;
        mScoreBoard = scoreBoard;
        mEntityRegistry = entityRegistry;
        mGameState = gameState;
        mMapRepository = mapRepository;

        mGameState.addListener(this);
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

        mMapInfo = mapInfo;
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

        loadGame(gameDescriptor);
        initializeMap(gameDescriptor.getMapDescriptor());
    }

    private void initializeMap(MapDescriptor mapDescriptor) {
        for (PlateauDescriptor descriptor : mapDescriptor.getPlateaus()) {
            Plateau plateau = (Plateau) mEntityRegistry.createEntity(descriptor.getName());
            plateau.setPosition(descriptor.getPosition());
            mGameEngine.add(plateau);
        }
    }

    public void loadGame(final GameDescriptor gameDescriptor) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    loadGame(gameDescriptor);
                }
            });
            return;
        }

        mGameEngine.clear();
        mGameEngine.setGameConfiguration(new GameConfiguration(gameDescriptor));
        mMapInfo = mMapRepository.getMapById(gameDescriptor.getMapId());
        mViewport.setGameSize(gameDescriptor.getMapDescriptor().getWidth(), gameDescriptor.getMapDescriptor().getHeight());
        mScoreBoard.reset(gameDescriptor.getGameSettings().getLives(), gameDescriptor.getGameSettings().getCredits());
    }

    @Override
    public void gameRestart() {
        loadMap(mMapInfo);
    }

    @Override
    public void gameOver() {

    }
}
