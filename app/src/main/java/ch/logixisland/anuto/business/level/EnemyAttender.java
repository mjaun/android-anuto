package ch.logixisland.anuto.business.level;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.EnemyListener;

class EnemyAttender {

    private final WaveAttender mWaveAttender;
    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;

    private final List<Enemy> mRemainingEnemies = new ArrayList<>();

    private final EnemyListener mEnemyListener = new EnemyListener() {
        @Override
        public void enemyKilled(Enemy enemy) {
            mScoreBoard.giveCredits(enemy.getReward(), true);
        }

        @Override
        public void enemyFinished(Enemy enemy) {
            mScoreBoard.takeLives(1);
        }

        @Override
        public void enemyRemoved(Enemy enemy) {
            mRemainingEnemies.remove(enemy);
            mWaveAttender.enemyRemoved(enemy);
        }
    };

    EnemyAttender(WaveAttender waveAttender, GameEngine gameEngine, ScoreBoard scoreBoard) {
        mWaveAttender = waveAttender;
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
    }

    void addEnemy(Enemy enemy, int delay) {
        mRemainingEnemies.add(enemy);
        enemy.addListener(mEnemyListener);

        final Enemy finalEnemy = enemy;
        mGameEngine.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGameEngine.add(finalEnemy);
            }
        }, delay);
    }

    int getRemainingEnemiesCount() {
        return mRemainingEnemies.size();
    }

    float getRemainingEnemiesReward() {
        float totalReward = 0f;

        for (Enemy enemy : mRemainingEnemies) {
            totalReward += enemy.getReward();
        }

        return totalReward;
    }

}
