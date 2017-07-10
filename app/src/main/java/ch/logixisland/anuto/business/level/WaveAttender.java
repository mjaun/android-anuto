package ch.logixisland.anuto.business.level;

import java.util.List;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.util.data.EnemyDescriptor;
import ch.logixisland.anuto.util.data.WaveDescriptor;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.vector.Vector2;

class WaveAttender {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final EnemyFactory mEnemyFactory;
    private final WaveManager mWaveManager;
    private final WaveDescriptor mWaveDescriptor;
    private final EnemyAttender mEnemyAttender;

    private int mExtend;
    private int mWaveReward;
    private float mEnemyHealthModifier;
    private float mEnemyRewardModifier;

    private boolean mNextWaveReady;

    WaveAttender(GameEngine gameEngine, ScoreBoard scoreBoard, EnemyFactory enemyFactory,
                 WaveManager waveManager, WaveDescriptor waveDescriptor) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mEnemyFactory = enemyFactory;
        mWaveManager = waveManager;
        mWaveDescriptor = waveDescriptor;

        mEnemyAttender = new EnemyAttender(this, mGameEngine, mScoreBoard);

        mExtend = 1;
        mWaveReward = mWaveDescriptor.getWaveReward();
        mEnemyHealthModifier = mWaveDescriptor.getHealthModifier();
        mEnemyRewardModifier = mWaveDescriptor.getRewardModifier();
    }

    int getExtend() {
        return mExtend;
    }

    float getEnemyHealthModifier() {
        return mEnemyHealthModifier;
    }

    float getEnemyRewardModifier() {
        return mEnemyRewardModifier;
    }

    WaveDescriptor getWaveDescriptor() {
        return mWaveDescriptor;
    }

    void setExtend(int extend) {
        mExtend = extend;
    }

    void modifyEnemyHealth(float modifier) {
        mEnemyHealthModifier *= modifier;
    }

    void modifyEnemyReward(float modifier) {
        mEnemyRewardModifier *= modifier;
    }

    void modifyWaveReward(float modifier) {
        mWaveReward *= modifier;
    }

    void start() {
        scheduleEnemies();
        mNextWaveReady = false;

        mGameEngine.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNextWaveReady = true;
                mWaveManager.checkNextWaveReady();
            }
        }, mWaveDescriptor.getNextWaveDelay());
    }

    void giveWaveReward() {
        mScoreBoard.giveCredits(mWaveReward, true);
        mWaveReward = 0;
    }

    float getRemainingEnemiesReward() {
        return mEnemyAttender.getRemainingEnemiesReward();
    }

    void enemyRemoved(Enemy enemy) {
        mWaveManager.enemyRemoved();

        if (mEnemyAttender.getRemainingEnemiesCount() == 0) {
            giveWaveReward();
            mWaveManager.waveFinished(this);
        }
    }

    boolean isNextWaveReady() {
        return mNextWaveReady;
    }

    private void scheduleEnemies() {
        int delay = 0;
        Vector2 offset = new Vector2();

        List<EnemyDescriptor> enemyDescriptors = mWaveDescriptor.getEnemies();

        for (int extendIndex = 0; extendIndex < mExtend + 1; extendIndex++) {
            for (EnemyDescriptor descriptor : enemyDescriptors) {
                if (MathUtils.equals(descriptor.getDelay(), 0f, 0.1f)) {
                    offset.add(descriptor.getOffset());
                } else {
                    offset.set(descriptor.getOffset());
                }

                Enemy enemy = createAndConfigureEnemy(offset, descriptor);

                if (extendIndex > 0 || enemyDescriptors.indexOf(descriptor) > 0) {
                    delay += (int) descriptor.getDelay();
                }

                mEnemyAttender.addEnemy(enemy, delay);
            }
        }

        int waveEnemiesCount = (mExtend + 1) * enemyDescriptors.size();
        mWaveManager.addWaveEnemiesCount(waveEnemiesCount);
    }

    private Enemy createAndConfigureEnemy(Vector2 offset, EnemyDescriptor descriptor) {
        final Enemy enemy = mEnemyFactory.createEnemy(descriptor.getName());
        enemy.modifyHealth(mEnemyHealthModifier);
        enemy.modifyReward(mEnemyRewardModifier);
        enemy.setPathIndex(descriptor.getPathIndex());
        enemy.move(offset);
        return enemy;
    }
}
