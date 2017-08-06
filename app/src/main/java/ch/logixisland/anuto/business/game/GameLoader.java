package ch.logixisland.anuto.business.game;

import android.content.Context;

import java.io.InputStream;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.descriptor.MapDescriptor;
import ch.logixisland.anuto.data.descriptor.PlateauDescriptor;
import ch.logixisland.anuto.data.descriptor.WavesDescriptor;
import ch.logixisland.anuto.data.setting.EnemySettings;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.setting.TowerSettings;
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
    private GameSettings mGameSettings;
    private TowerSettings mTowerSettings;
    private EnemySettings mEnemySettings;
    private MapDescriptor mMapDescriptor;
    private WavesDescriptor mWavesDescriptor;
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
            mGameSettings = GameSettings.fromXml(mContext.getResources().openRawResource(R.raw.game_settings));
            mTowerSettings = TowerSettings.fromXml(mContext.getResources().openRawResource(R.raw.tower_settings));
            mEnemySettings = EnemySettings.fromXml(mContext.getResources().openRawResource(R.raw.enemy_settings));
            mWavesDescriptor = WavesDescriptor.fromXml(mContext.getResources().openRawResource(R.raw.wave_descriptors));
        } catch (Exception e) {
            throw new RuntimeException("Could not load settings!", e);
        }

        mTowerFactory.setTowerSettings(mTowerSettings);
        mEnemyFactory.configureFactory(mEnemySettings, mGameSettings);
        mGameState.addListener(this);
    }

    public MapInfo getMapInfo() {
        return mMapInfo;
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public TowerSettings getTowerSettings() {
        return mTowerSettings;
    }

    public EnemySettings getEnemySettings() {
        return mEnemySettings;
    }

    public MapDescriptor getMapDescriptor() {
        return mMapDescriptor;
    }

    public WavesDescriptor getWavesDescriptor() {
        return mWavesDescriptor;
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
            mMapDescriptor = MapDescriptor.fromXml(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Could not load map!", e);
        }

        mTowerFactory.setPaths(mMapDescriptor.getPaths());
        mGameState.restart();
    }

    @Override
    public void gameRestart() {
        mGameEngine.clear();

        for (PlateauDescriptor descriptor : mMapDescriptor.getPlateaus()) {
            Plateau p = mPlateauFactory.createPlateau(descriptor.getName());
            p.setPosition(descriptor.getPosition());
            mGameEngine.add(p);
        }

        mViewport.setGameSize(mMapDescriptor.getWidth(), mMapDescriptor.getHeight());
        mScoreBoard.reset(mGameSettings.getLives(), mGameSettings.getCredits());
    }

    @Override
    public void gameOver() {

    }

}
