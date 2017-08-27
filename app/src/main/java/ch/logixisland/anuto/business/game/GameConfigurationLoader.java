package ch.logixisland.anuto.business.game;

import android.content.Context;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.data.game.GameDescriptorRoot;
import ch.logixisland.anuto.data.map.MapDescriptorRoot;
import ch.logixisland.anuto.data.map.MapInfo;
import ch.logixisland.anuto.data.map.MapRepository;
import ch.logixisland.anuto.data.map.PlateauDescriptor;
import ch.logixisland.anuto.data.setting.GameSettingsRoot;
import ch.logixisland.anuto.data.setting.enemy.EnemySettingsRoot;
import ch.logixisland.anuto.data.setting.tower.TowerSettingsRoot;
import ch.logixisland.anuto.data.wave.WaveDescriptorRoot;
import ch.logixisland.anuto.engine.logic.GameConfiguration;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.plateau.Plateau;

public class GameConfigurationLoader implements Persister, GameStateListener {

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;
    private final EntityRegistry mEntityRegistry;
    private final GameState mGameState;
    private final MapRepository mMapRepository;

    private MapInfo mMapInfo;

    public GameConfigurationLoader(Context context, GameEngine gameEngine, ScoreBoard scoreBoard,
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

        setGameConfiguration(mMapRepository.getMapInfos().get(0));
    }

    public MapInfo getMapInfo() {
        return mMapInfo;
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

        if (mMapInfo == mapInfo) {
            return;
        }

        setGameConfiguration(mapInfo);
        mGameState.restart();
    }

    private void setGameConfiguration(MapInfo mapInfo) {
        mMapInfo = mapInfo;

        try {
            mGameEngine.setGameConfiguration(new GameConfiguration(
                    GameSettingsRoot.fromXml(mContext, R.raw.game_settings),
                    EnemySettingsRoot.fromXml(mContext, R.raw.enemy_settings),
                    TowerSettingsRoot.fromXml(mContext, R.raw.tower_settings),
                    MapDescriptorRoot.fromXml(mContext, mapInfo.getMapDescriptorResId()),
                    WaveDescriptorRoot.fromXml(mContext, R.raw.wave_descriptors)
            ));
        } catch (Exception e) {
            throw new RuntimeException("Could not load map!", e);
        }
    }

    @Override
    public void gameRestart() {
        mGameEngine.clear();

        GameConfiguration configuration = mGameEngine.getGameConfiguration();

        for (PlateauDescriptor descriptor : configuration.getMapDescriptorRoot().getPlateaus()) {
            Plateau plateau = (Plateau) mEntityRegistry.createEntity(descriptor.getName());
            plateau.setPosition(descriptor.getPosition());
            mGameEngine.add(plateau);
        }

        mViewport.setGameSize(configuration.getMapDescriptorRoot().getWidth(), configuration.getMapDescriptorRoot().getHeight());
        mScoreBoard.reset(configuration.getGameSettingsRoot().getLives(), configuration.getGameSettingsRoot().getCredits());
    }

    @Override
    public void gameOver() {

    }

    @Override
    public void writeDescriptor(GameDescriptorRoot gameDescriptor) {
        gameDescriptor.setMapId(mMapInfo.getMapId());
    }

    @Override
    public void readDescriptor(GameDescriptorRoot gameDescriptor) {
        setGameConfiguration(mMapRepository.getMapById(gameDescriptor.getMapId()));
        GameConfiguration configuration = mGameEngine.getGameConfiguration();
        mViewport.setGameSize(configuration.getMapDescriptorRoot().getWidth(), configuration.getMapDescriptorRoot().getHeight());
    }
}
