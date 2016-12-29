package ch.logixisland.anuto.business.level;

import java.util.List;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.data.EnemyDescriptor;
import ch.logixisland.anuto.util.data.WaveDescriptor;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.math.MathUtils;

class WaveAttender {

    private final WaveManager mWaveManager;
    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final WaveDescriptor mWaveDescriptor;

    private final EnemyAttender mEnemyAttender;

    private int mExtend;
    private int mWaveReward;
    private float mEnemyHealthModifier;
    private float mEnemyRewardModifier;

    private boolean mNextWaveReady;

    WaveAttender(WaveManager waveManager, GameEngine gameEngine, ScoreBoard scoreBoard, WaveDescriptor waveDescriptor) {
        mWaveManager = waveManager;
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
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
                notifyNextWaveReady();
            }
        }, mWaveDescriptor.getNextWaveDelay());
    }

    void giveWaveReward() {
        mScoreBoard.giveCredits(mWaveReward);
        mWaveReward = 0;
    }

    float getRemainingEnemiesReward() {
        return mEnemyAttender.getRemainingEnemiesReward();
    }

    void enemyRemoved(Enemy enemy) {
        mWaveManager.enemyRemoved();

        if (mEnemyAttender.getRemainingEnemiesCount() == 0) {
            giveWaveReward();
            notifyNextWaveReady();
            mWaveManager.waveFinished(this);
        }
    }

    private void scheduleEnemies() {
        int delay = 0;
        float offsetX = 0f;
        float offsetY = 0f;

        List<EnemyDescriptor> enemyDescriptors = mWaveDescriptor.getEnemies();

        for (int extendIndex = 0; extendIndex < mExtend + 1; extendIndex++) {
            for (EnemyDescriptor descriptor : enemyDescriptors) {
                if (MathUtils.equals(descriptor.getDelay(), 0f, 0.1f)) {
                    offsetX += descriptor.getOffsetX();
                    offsetY += descriptor.getOffsetY();
                } else {
                    offsetX = descriptor.getOffsetX();
                    offsetY = descriptor.getOffsetY();
                }

                Enemy enemy = createAndConfigureEnemy(offsetX, offsetY, descriptor);

                if (extendIndex > 0 || enemyDescriptors.indexOf(descriptor) > 0) {
                    delay += (int)descriptor.getDelay();
                }

                mEnemyAttender.addEnemy(enemy, delay);
            }
        }
    }

    private Enemy createAndConfigureEnemy(float offsetX, float offsetY, EnemyDescriptor descriptor) {
        final Enemy enemy = descriptor.createInstance();
        enemy.modifyHealth(mEnemyHealthModifier);
        enemy.modifyReward(mEnemyRewardModifier);
        enemy.setPathIndex(descriptor.getPathIndex());
        enemy.move(offsetX, offsetY);
        return enemy;
    }

    private void notifyNextWaveReady() {
        if (!mNextWaveReady) {
            mNextWaveReady = true;
            mWaveManager.nextWaveReady();
        }
    }
}
