package ch.logixisland.anuto.business.level;

import android.content.Context;

import java.io.InputStream;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.GameStateListener;
import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.data.descriptor.LevelDescriptor;
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

public class LevelLoader implements GameStateListener {

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;
    private final PlateauFactory mPlateauFactory;
    private final TowerFactory mTowerFactory;
    private final EnemyFactory mEnemyFactory;

    private LevelInfo mLevelInfo;
    private GameSettings mGameSettings;
    private TowerSettings mTowerSettings;
    private EnemySettings mEnemySettings;
    private LevelDescriptor mLevelDescriptor;
    private WavesDescriptor mWavesDescriptor;
    private GameState mGameState;

    public LevelLoader(Context context, GameEngine gameEngine, ScoreBoard scoreBoard,
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
            mWavesDescriptor = WavesDescriptor.fromXml(mContext.getResources().openRawResource(R.raw.waves));
        } catch (Exception e) {
            throw new RuntimeException("Could not load settings!", e);
        }

        mTowerFactory.setTowerSettings(mTowerSettings);
        mEnemyFactory.setEnemySettings(mEnemySettings);
        mGameState.addListener(this);
    }

    public LevelInfo getLevelInfo() {
        return mLevelInfo;
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

    public LevelDescriptor getLevelDescriptor() {
        return mLevelDescriptor;
    }

    public WavesDescriptor getWavesDescriptor() {
        return mWavesDescriptor;
    }

    public void loadLevel(final LevelInfo levelInfo) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    loadLevel(levelInfo);
                }
            });
            return;
        }

        if (mLevelInfo == levelInfo) {
            return;
        }

        mLevelInfo = levelInfo;

        try {
            InputStream inputStream = mContext.getResources().openRawResource(mLevelInfo.getLevelDataResId());
            mLevelDescriptor = LevelDescriptor.fromXml(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Could not load level!", e);
        }

        mTowerFactory.setPaths(mLevelDescriptor.getPaths());
        mGameState.restart();
    }

    @Override
    public void gameRestart() {
        mGameEngine.clear();

        for (PlateauDescriptor descriptor : mLevelDescriptor.getPlateaus()) {
            Plateau p = mPlateauFactory.createPlateau(descriptor.getName());
            p.setPosition(descriptor.getPosition());
            mGameEngine.add(p);
        }

        mViewport.setGameSize(mLevelDescriptor.getWidth(), mLevelDescriptor.getHeight());
        mScoreBoard.reset(mGameSettings.getLives(), mGameSettings.getCredits());
    }

    @Override
    public void gameOver() {

    }

}
