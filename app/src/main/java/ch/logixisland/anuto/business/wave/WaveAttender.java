package ch.logixisland.anuto.business.wave;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.business.game.GameLoader;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.data.map.PathDescriptor;
import ch.logixisland.anuto.data.wave.EnemyDescriptor;
import ch.logixisland.anuto.data.wave.WaveDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.Message;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.entity.enemy.EnemyListener;
import ch.logixisland.anuto.util.math.MathUtils;

class WaveAttender implements EnemyListener {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final GameLoader mGameLoader;
    private final EnemyFactory mEnemyFactory;
    private final WaveManager mWaveManager;
    private final WaveDescriptor mWaveDescriptor;

    private final List<Enemy> mRemainingEnemies = new ArrayList<>();

    private int mExtend;
    private int mWaveReward;
    private float mEnemyHealthModifier;
    private float mEnemyRewardModifier;

    WaveAttender(GameEngine gameEngine, ScoreBoard scoreBoard, GameLoader gameLoader,
                 EnemyFactory enemyFactory, WaveManager waveManager, WaveDescriptor waveDescriptor) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mGameLoader = gameLoader;
        mEnemyFactory = enemyFactory;
        mWaveManager = waveManager;
        mWaveDescriptor = waveDescriptor;

        mExtend = 1;
        mEnemyHealthModifier = 1;
        mEnemyRewardModifier = 1;
        mWaveReward = mWaveDescriptor.getWaveReward();
    }

    int getExtend() {
        return mExtend;
    }

    void setExtend(int extend) {
        mExtend = extend;
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
    }

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
        mWaveManager.enemyRemoved();

        if (getRemainingEnemiesCount() == 0) {
            giveWaveReward();
            mWaveManager.waveFinished(this);
        }
    }

    void giveWaveReward() {
        mScoreBoard.giveCredits(mWaveReward, true);
        mWaveReward = 0;
    }

    int getWaveReward() {
        return mWaveReward;
    }

    float getRemainingEnemiesReward() {
        float totalReward = 0f;

        for (Enemy enemy : mRemainingEnemies) {
            totalReward += enemy.getReward();
        }

        return totalReward;
    }

    int getRemainingEnemiesCount() {
        return mRemainingEnemies.size();
    }

    private void scheduleEnemies() {
        int delay = 0;
        float offset = 0;

        List<EnemyDescriptor> enemyDescriptors = mWaveDescriptor.getEnemies();

        for (int extendIndex = 0; extendIndex < mExtend + 1; extendIndex++) {
            for (int enemyIndex = 0; enemyIndex < enemyDescriptors.size(); enemyIndex++) {
                EnemyDescriptor descriptor = enemyDescriptors.get(enemyIndex);

                if (MathUtils.equals(descriptor.getDelay(), 0f, 0.1f)) {
                    offset += descriptor.getOffset();
                } else {
                    offset = descriptor.getOffset();
                }

                if (enemyIndex > 0 || extendIndex > 0) {
                    delay += (int) descriptor.getDelay();
                }

                Enemy enemy = createAndConfigureEnemy(descriptor, offset);
                addEnemy(enemy, delay);
            }
        }
    }

    private Enemy createAndConfigureEnemy(EnemyDescriptor descriptor, float offset) {
        PathDescriptor path = mGameLoader.getMapDescriptorRoot().getPaths().get(descriptor.getPathIndex());
        Enemy enemy = mEnemyFactory.createEnemy(descriptor.getName(), mEnemyHealthModifier, mEnemyRewardModifier);
        enemy.setupPath(path.getWayPoints(), offset);
        return enemy;
    }

    private void addEnemy(final Enemy enemy, int delay) {
        mRemainingEnemies.add(enemy);
        enemy.addListener(this);

        mGameEngine.postDelayed(new Message() {
            @Override
            public void execute() {
                mGameEngine.add(enemy);
            }
        }, delay);
    }
}
