package ch.logixisland.anuto.business.level;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.entity.plateau.PlateauFactory;
import ch.logixisland.anuto.entity.tower.TowerFactory;
import ch.logixisland.anuto.util.data.EnemySettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;
import ch.logixisland.anuto.util.data.PlateauDescriptor;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.util.data.TowerSettings;

public class LevelLoader {

    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;
    private final PlateauFactory mPlateauFactory;

    private GameSettings mGameSettings;
    private TowerSettings mTowerSettings;
    private EnemySettings mEnemySettings;
    private LevelDescriptor mLevelDescriptor;

    public LevelLoader(GameEngine gameEngine, Viewport viewport, ScoreBoard scoreBoard,
                       PlateauFactory plateauFactory) {
        mGameEngine = gameEngine;
        mViewport = viewport;
        mScoreBoard = scoreBoard;
        mPlateauFactory = plateauFactory;
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        mGameSettings = gameSettings;
    }

    public TowerSettings getTowerSettings() {
        return mTowerSettings;
    }

    public void setTowerSettings(TowerSettings towerSettings) {
        mTowerSettings = towerSettings;
    }

    public EnemySettings getEnemySettings() {
        return mEnemySettings;
    }

    public void setEnemySettings(EnemySettings enemySettings) {
        mEnemySettings = enemySettings;
    }

    public LevelDescriptor getLevelDescriptor() {
        return mLevelDescriptor;
    }

    public void setLevelDescriptor(LevelDescriptor levelDescriptor) {
        mLevelDescriptor = levelDescriptor;
    }

    public void reset() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            });
            return;
        }

        mGameEngine.clear();

        for (PlateauDescriptor descriptor : mLevelDescriptor.getPlateaus()) {
            Plateau p = mPlateauFactory.createPlateau(descriptor.getName());
            p.setPosition(descriptor.getPosition());
            mGameEngine.add(p);
        }

        mViewport.setGameSize(mLevelDescriptor.getWidth(), mLevelDescriptor.getHeight());
        mScoreBoard.reset(mGameSettings.getLives(), mGameSettings.getCredits());
    }

}
