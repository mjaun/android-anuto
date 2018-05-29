package ch.logixisland.anuto.business.wave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.data.map.MapPath;
import ch.logixisland.anuto.data.state.ActiveWaveData;
import ch.logixisland.anuto.data.wave.EnemyInfo;
import ch.logixisland.anuto.data.wave.WaveInfo;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.EnemyListener;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.Vector2;

class WaveAttender implements EnemyListener {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final EntityRegistry mEntityRegistry;
    private final WaveManager mWaveManager;
    private final WaveInfo mWaveInfo;

    private final Collection<Enemy> mRemainingEnemies = new ArrayList<>();

    private int mWaveNumber;
    private int mWaveStartTickCount;

    private int mExtend;
    private int mWaveReward;
    private float mEnemyHealthModifier;
    private float mEnemyRewardModifier;

    WaveAttender(GameEngine gameEngine, ScoreBoard scoreBoard, EntityRegistry entityRegistry,
                 WaveManager waveManager, WaveInfo waveInfo, int waveNumber) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mEntityRegistry = entityRegistry;
        mWaveManager = waveManager;
        mWaveInfo = waveInfo;
        mWaveNumber = waveNumber;

        mExtend = 1;
        mEnemyHealthModifier = 1;
        mEnemyRewardModifier = 1;
        mWaveReward = mWaveInfo.getWaveReward();
    }

    float getWaveDefaultHealth(EnemyDefaultHealth enemyDefaultHealth) {
        float waveHealth = 0f;
        for (EnemyInfo d : mWaveInfo.getEnemies()) {
            waveHealth += enemyDefaultHealth.getDefaultHealth(d.getName());
        }
        waveHealth *= mExtend + 1;
        return waveHealth;
    }

    int getWaveReward() {
        return mWaveReward;
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
        if (mWaveStartTickCount == 0) {
            mWaveStartTickCount = mGameEngine.getTickCount();
        }

        scheduleEnemies();
    }

    void giveWaveReward() {
        mScoreBoard.giveCredits(mWaveReward, true);
        mWaveReward = 0;
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

    ActiveWaveData writeActiveWaveDescriptor() {
        ActiveWaveData descriptor = new ActiveWaveData();
        descriptor.setWaveNumber(mWaveNumber);
        descriptor.setWaveStartTickCount(mWaveStartTickCount);
        descriptor.setExtend(mExtend);
        descriptor.setWaveReward(mWaveReward);
        descriptor.setEnemyHealthModifier(mEnemyHealthModifier);
        descriptor.setEnemyRewardModifier(mEnemyRewardModifier);
        return descriptor;
    }

    void readActiveWaveDescriptor(ActiveWaveData activeWaveData) {
        mExtend = activeWaveData.getExtend();
        mWaveReward = activeWaveData.getWaveReward();
        mEnemyHealthModifier = activeWaveData.getEnemyHealthModifier();
        mEnemyRewardModifier = activeWaveData.getEnemyRewardModifier();
        mWaveStartTickCount = activeWaveData.getWaveStartTickCount();

        StreamIterator<Enemy> enemyIterator = mGameEngine.getEntitiesByType(Types.ENEMY).cast(Enemy.class);
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            if (enemy.getWaveNumber() == mWaveNumber) {
                mRemainingEnemies.add(enemy);
                enemy.addListener(this);
            }
        }
    }

    private void scheduleEnemies() {
        int delayTicks = mWaveStartTickCount - mGameEngine.getTickCount();
        float offset = 0;

        List<EnemyInfo> enemyInfos = mWaveInfo.getEnemies();

        for (int extendIndex = 0; extendIndex < mExtend + 1; extendIndex++) {
            for (int enemyIndex = 0; enemyIndex < enemyInfos.size(); enemyIndex++) {
                EnemyInfo descriptor = enemyInfos.get(enemyIndex);

                if (MathUtils.equals(descriptor.getDelay(), 0f, 0.1f)) {
                    offset += descriptor.getOffset();
                } else {
                    offset = descriptor.getOffset();
                }

                if (enemyIndex > 0 || extendIndex > 0) {
                    delayTicks += Math.round(descriptor.getDelay() * GameEngine.TARGET_FRAME_RATE);
                }

                if (delayTicks >= 0) {
                    Enemy enemy = createAndConfigureEnemy(descriptor, offset);
                    addEnemy(enemy, delayTicks);
                }
            }
        }
    }

    private Enemy createAndConfigureEnemy(EnemyInfo descriptor, float offset) {
        MapPath path = mGameEngine.getGameConfiguration().getMapDescriptor().getPaths().get(descriptor.getPathIndex());
        Enemy enemy = (Enemy) mEntityRegistry.createEntity(descriptor.getName());
        enemy.setWaveNumber(mWaveNumber);
        enemy.modifyHealth(mEnemyHealthModifier);
        enemy.modifyReward(mEnemyRewardModifier);
        enemy.setupPath(path.getWayPoints());

        Vector2 startPosition = path.getWayPoints().get(0);
        Vector2 startDirection = startPosition.to(path.getWayPoints().get(1)).norm();
        enemy.setPosition(startPosition.add(startDirection.mul(-offset)));

        return enemy;
    }

    private void addEnemy(final Enemy enemy, int delayTicks) {
        mRemainingEnemies.add(enemy);
        enemy.addListener(this);

        mGameEngine.postAfterTicks(new Message() {
            @Override
            public void execute() {
                mGameEngine.add(enemy);
            }
        }, delayTicks);
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
}
