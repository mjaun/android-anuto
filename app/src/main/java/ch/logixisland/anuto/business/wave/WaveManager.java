package ch.logixisland.anuto.business.wave;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.business.tower.TowerAging;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.map.GameMap;
import ch.logixisland.anuto.engine.logic.map.MapPath;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class WaveManager implements Persister {

    private static final String TAG = WaveManager.class.getSimpleName();

    private static final int MAX_WAVES_IN_GAME = 3;
    private static final float MIN_WAVE_DELAY = 5;

    public interface Listener {
        void waveNumberChanged();
        void nextWaveReadyChanged();
        void remainingEnemiesCountChanged();
    }

    public interface WaveStartedListener {
        void waveStarted();
    }

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final GameState mGameState;
    private final TowerAging mTowerAging;
    private final EntityRegistry mEntityRegistry;
    private final EnemyDefaultHealth mEnemyDefaultHealth;

    private int mWaveNumber;
    private int mRemainingEnemiesCount;
    private boolean mNextWaveReady;
    private boolean mMinWaveDelayTimeout;

    private float mDifficultyLinear;
    private float mDifficultyModifier;
    private float mMinHealthModifier;
    private float mMinRewardModifier;
    private float mRewardModifier;
    private float mDifficultyExponent;
    private float mRewardExponent;
    private float mEarlyModifier;
    private float mEarlyExponent;

    private List<MapPath> mPaths;

    private final List<WaveInfo> mWaveInfos = new ArrayList<>();
    private final List<WaveAttender> mActiveWaves = new ArrayList<>();
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();
    private final List<WaveStartedListener> mWaveStartedListeners = new CopyOnWriteArrayList<>();

    public WaveManager(GameEngine gameEngine, ScoreBoard scoreBoard, GameState gameState,
                       EntityRegistry entityRegistry, TowerAging towerAging) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mGameState = gameState;
        mTowerAging = towerAging;
        mEntityRegistry = entityRegistry;

        mEnemyDefaultHealth = new EnemyDefaultHealth(entityRegistry);
    }

    public int getWaveNumber() {
        return mWaveNumber;
    }

    public boolean isNextWaveReady() {
        return mNextWaveReady;
    }

    public int getRemainingEnemiesCount() {
        return mRemainingEnemiesCount;
    }

    public void startNextWave() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    startNextWave();
                }
            });
            return;
        }

        if (!mNextWaveReady) {
            return;
        }

        mGameState.gameStarted();

        giveWaveRewardAndEarlyBonus();
        createAndStartWaveAttender();
        updateBonusOnScoreBoard();
        updateRemainingEnemiesCount();

        setWaveNumber(mWaveNumber + 1);
        setNextWaveReady(false);
        triggerMinWaveDelay();

        for (WaveStartedListener listener : mWaveStartedListeners) {
            listener.waveStarted();
        }
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    public void addListener(WaveStartedListener listener) {
        mWaveStartedListeners.add(listener);
    }

    public void removeListener(WaveStartedListener listener) {
        mWaveStartedListeners.remove(listener);
    }

    @Override
    public void resetState(KeyValueStore gameConfig) {
        initializeConfig(gameConfig);
        initializeWaveInfos(gameConfig);
        setWaveNumber(0);
        mActiveWaves.clear();
        setNextWaveReady(true);
    }

    @Override
    public void writeState(KeyValueStore gameState) {
        gameState.putInt("waveNumber", mWaveNumber);

        for (WaveAttender waveAttender : mActiveWaves) {
            gameState.appendStore("activeWaves", waveAttender.writeActiveWaveData());
        }
    }

    @Override
    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        initializeConfig(gameConfig);
        initializeWaveInfos(gameConfig);
        initializeActiveWaves(gameState);
        initializeNextWaveReady(gameState);
        setWaveNumber(gameState.getInt("waveNumber"));
        updateRemainingEnemiesCount();
    }

    public List<MapPath> getPaths() {
        return mPaths;
    }

    private void initializeConfig(KeyValueStore gameConfig) {
        mDifficultyLinear = gameConfig.getFloat("difficultyLinear");
        mDifficultyModifier = gameConfig.getFloat("difficultyModifier");
        mDifficultyExponent = gameConfig.getFloat("difficultyExponent");
        mMinHealthModifier = gameConfig.getFloat("minHealthModifier");
        mRewardModifier = gameConfig.getFloat("rewardModifier");
        mRewardExponent = gameConfig.getFloat("rewardExponent");
        mMinRewardModifier = gameConfig.getFloat("minRewardModifier");
        mEarlyModifier = gameConfig.getFloat("earlyModifier");
        mEarlyExponent = gameConfig.getFloat("earlyExponent");
        mPaths = new GameMap(gameConfig).getPaths();
    }

    private void initializeWaveInfos(KeyValueStore gameConfig) {
        mWaveInfos.clear();

        for (KeyValueStore data : gameConfig.getStoreList("waves")) {
            mWaveInfos.add(new WaveInfo(data));
        }
    }

    private void initializeActiveWaves(KeyValueStore gameState) {
        mActiveWaves.clear();

        for (KeyValueStore activeWaveData : gameState.getStoreList("activeWaves")) {
            WaveInfo waveInfo = mWaveInfos.get(activeWaveData.getInt("waveNumber") % mWaveInfos.size());
            WaveAttender waveAttender = new WaveAttender(mGameEngine, mScoreBoard, mEntityRegistry, this, waveInfo, mPaths, activeWaveData.getInt("waveNumber"));
            waveAttender.readActiveWaveData(activeWaveData);
            waveAttender.start();
            mActiveWaves.add(waveAttender);
        }
    }

    private void initializeNextWaveReady(KeyValueStore gameState) {
        int minWaveDelayTicks = Math.round(MIN_WAVE_DELAY * GameEngine.TARGET_FRAME_RATE);
        int lastStartedWaveTickCount = -minWaveDelayTicks;

        for (KeyValueStore activeWaveData : gameState.getStoreList("activeWaves")) {
            lastStartedWaveTickCount = Math.max(lastStartedWaveTickCount, activeWaveData.getInt("waveStartTickCount"));
        }

        int nextWaveReadyTicks = minWaveDelayTicks - (mGameEngine.getTickCount() - lastStartedWaveTickCount);

        if (nextWaveReadyTicks > 0) {
            setNextWaveReady(false);
            mMinWaveDelayTimeout = false;

            mGameEngine.postAfterTicks(new Message() {
                @Override
                public void execute() {
                    mMinWaveDelayTimeout = true;
                    updateNextWaveReady();
                }
            }, nextWaveReadyTicks);
        } else {
            setNextWaveReady(true);
        }
    }

    void enemyRemoved() {
        updateBonusOnScoreBoard();
        updateRemainingEnemiesCount();
    }

    void waveFinished(WaveAttender waveAttender) {
        mActiveWaves.remove(waveAttender);

        mTowerAging.ageTowers();
        updateBonusOnScoreBoard();
        updateNextWaveReady();
    }

    private void giveWaveRewardAndEarlyBonus() {
        WaveAttender currentWave = getCurrentWave();

        if (currentWave != null) {
            currentWave.giveWaveReward();
            mScoreBoard.giveCredits(getEarlyBonus(), false);
        }
    }

    private void triggerMinWaveDelay() {
        mMinWaveDelayTimeout = false;

        mGameEngine.postDelayed(new Message() {
            @Override
            public void execute() {
                mMinWaveDelayTimeout = true;
                updateNextWaveReady();
            }
        }, MIN_WAVE_DELAY);
    }

    private void updateNextWaveReady() {
        if (mNextWaveReady) {
            return;
        }

        if (!mMinWaveDelayTimeout) {
            return;
        }

        if (mActiveWaves.size() >= MAX_WAVES_IN_GAME) {
            return;
        }

        setNextWaveReady(true);
    }

    private void updateBonusOnScoreBoard() {
        mScoreBoard.setEarlyBonus(getEarlyBonus());

        WaveAttender currentWave = getCurrentWave();
        if (currentWave != null) {
            mScoreBoard.setWaveBonus(currentWave.getWaveReward());
        } else {
            mScoreBoard.setWaveBonus(0);
        }
    }

    private void updateRemainingEnemiesCount() {
        int totalCount = 0;

        for (WaveAttender waveAttender : mActiveWaves) {
            totalCount += waveAttender.getRemainingEnemiesCount();
        }

        if (mRemainingEnemiesCount != totalCount) {
            mRemainingEnemiesCount = totalCount;

            for (Listener listener : mListeners) {
                listener.remainingEnemiesCountChanged();
            }
        }
    }

    private void createAndStartWaveAttender() {
        WaveInfo nextWaveInfo = mWaveInfos.get(mWaveNumber % mWaveInfos.size());
        WaveAttender nextWave = new WaveAttender(mGameEngine, mScoreBoard, mEntityRegistry, this, nextWaveInfo, mPaths, mWaveNumber);
        updateWaveExtend(nextWave, nextWaveInfo);
        updateWaveModifiers(nextWave);
        nextWave.start();
        mActiveWaves.add(nextWave);
    }

    private void updateWaveExtend(WaveAttender wave, WaveInfo waveInfo) {
        int extend = Math.min((getIterationNumber() - 1) * waveInfo.getExtend(), waveInfo.getMaxExtend());
        wave.setExtend(extend);
    }

    private void updateWaveModifiers(WaveAttender wave) {
        float waveHealth = wave.getWaveDefaultHealth(this.mEnemyDefaultHealth);
        float damagePossible = mDifficultyLinear * mScoreBoard.getCreditsEarned()
                + mDifficultyModifier * (float) Math.pow(mScoreBoard.getCreditsEarned(), mDifficultyExponent);
        float healthModifier = damagePossible / waveHealth;
        healthModifier = Math.max(healthModifier, mMinHealthModifier);

        float rewardModifier = mRewardModifier * (float) Math.pow(healthModifier, mRewardExponent);
        rewardModifier = Math.max(rewardModifier, mMinRewardModifier);

        wave.modifyEnemyHealth(healthModifier);
        wave.modifyEnemyReward(rewardModifier);
        wave.modifyWaveReward(getIterationNumber());

        Log.i(TAG, String.format("waveNumber=%d", getWaveNumber()));
        Log.i(TAG, String.format("waveHealth=%f", waveHealth));
        Log.i(TAG, String.format("creditsEarned=%d", mScoreBoard.getCreditsEarned()));
        Log.i(TAG, String.format("damagePossible=%f", damagePossible));
        Log.i(TAG, String.format("healthModifier=%f", healthModifier));
        Log.i(TAG, String.format("rewardModifier=%f", rewardModifier));
    }

    private int getIterationNumber() {
        return (getWaveNumber() / mWaveInfos.size()) + 1;
    }

    private int getEarlyBonus() {
        float remainingReward = 0;

        for (WaveAttender wave : mActiveWaves) {
            remainingReward += wave.getRemainingEnemiesReward();
        }

        return Math.round(mEarlyModifier * (float) Math.pow(remainingReward, mEarlyExponent));
    }

    private WaveAttender getCurrentWave() {
        if (mActiveWaves.isEmpty()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }

    private void setWaveNumber(int waveNumber) {
        if (mWaveNumber != waveNumber) {
            mWaveNumber = waveNumber;

            for (Listener listener : mListeners) {
                listener.waveNumberChanged();
            }
        }
    }

    private void setNextWaveReady(boolean ready) {
        if (mNextWaveReady != ready) {
            mNextWaveReady = ready;

            for (Listener listener : mListeners) {
                listener.nextWaveReadyChanged();
            }
        }
    }
}
