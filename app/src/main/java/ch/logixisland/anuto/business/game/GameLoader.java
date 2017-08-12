package ch.logixisland.anuto.business.game;

import android.content.Context;

import java.io.InputStream;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.data.map.MapDescriptorRoot;
import ch.logixisland.anuto.data.map.MapInfo;
import ch.logixisland.anuto.data.map.PlateauDescriptor;
import ch.logixisland.anuto.data.setting.enemy.EnemySettingsRoot;
import ch.logixisland.anuto.data.setting.game.GameSettingsRoot;
import ch.logixisland.anuto.data.setting.tower.TowerSettingsRoot;
import ch.logixisland.anuto.data.wave.WaveDescriptorRoot;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.plateau.PlateauFactory;
import ch.logixisland.anuto.entity.tower.TowerFactory;

public class GameLoader implements GameStateListener {

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;
    private final PlateauFactory mPlateauFactory;
    private final TowerFactory mTowerFactory;
    private final EnemyFactory mEnemyFactory;

    private MapInfo mMapInfo;
    private GameSettingsRoot mGameSettingsRoot;
    private TowerSettingsRoot mTowerSettingsRoot;
    private EnemySettingsRoot mEnemySettingsRoot;
    private MapDescriptorRoot mMapDescriptorRoot;
    private WaveDescriptorRoot mWaveDescriptorRoot;
    private GameState mGameState;

    public GameLoader(Context context, GameEngine gameEngine, ScoreBoard scoreBoard,
                      GameState gameState, Viewport viewport,
                      PlateauFactory plateauFactory, TowerFactory towerFactory,
                      EnemyFactory enemyFactory) {
        mContext = context;
        mGameEngine = gameEngine;
        mViewport = viewport;
        mScoreBoard = scoreBoard;
        mPlateauFactory = plateauFactory;
        mGameState = gameState;
        mTowerFactory = towerFactory;
        mEnemyFactory = enemyFactory;

        try {
            mGameSettingsRoot = GameSettingsRoot.fromXml(mContext.getResources().openRawResource(R.raw.game_settings));
            mTowerSettingsRoot = TowerSettingsRoot.fromXml(mContext.getResources().openRawResource(R.raw.tower_settings));
            mEnemySettingsRoot = EnemySettingsRoot.fromXml(mContext.getResources().openRawResource(R.raw.enemy_settings));
            mWaveDescriptorRoot = WaveDescriptorRoot.fromXml(mContext.getResources().openRawResource(R.raw.wave_descriptors));
        } catch (Exception e) {
            throw new RuntimeException("Could not load settings!", e);
        }

        mTowerFactory.setTowerSettingsRoot(mTowerSettingsRoot);
        mEnemyFactory.setEnemySettingsRoot(mEnemySettingsRoot);
        mGameState.addListener(this);
    }

    public MapInfo getMapInfo() {
        return mMapInfo;
    }

    public GameSettingsRoot getGameSettingsRoot() {
        return mGameSettingsRoot;
    }

    public MapDescriptorRoot getMapDescriptorRoot() {
        return mMapDescriptorRoot;
    }

    public WaveDescriptorRoot getWaveDescriptorRoot() {
        return mWaveDescriptorRoot;
    }

    public void loadMap(final MapInfo mapInfo) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    loadMap(mapInfo);
                }
            });
            return;
        }

        if (mMapInfo == mapInfo) {
            return;
        }

        mMapInfo = mapInfo;

        try {
            InputStream inputStream = mContext.getResources().openRawResource(mMapInfo.getMapDescriptorResId());
            mMapDescriptorRoot = MapDescriptorRoot.fromXml(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Could not load map!", e);
        }

        mTowerFactory.setPaths(mMapDescriptorRoot.getPaths());
        mGameState.restart();
    }

    @Override
    public void gameRestart() {
        mGameEngine.clear();

        for (PlateauDescriptor descriptor : mMapDescriptorRoot.getPlateaus()) {
            Plateau p = mPlateauFactory.createPlateau(descriptor.getName());
            p.setPosition(descriptor.getPosition());
            mGameEngine.add(p);
        }

        mViewport.setGameSize(mMapDescriptorRoot.getWidth(), mMapDescriptorRoot.getHeight());
        mScoreBoard.reset(mGameSettingsRoot.getLives(), mGameSettingsRoot.getCredits());
    }

    @Override
    public void gameOver() {

    }

}
