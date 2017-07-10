package ch.logixisland.anuto.business.level;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.util.data.EnemyDescriptor;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.WaveDescriptor;
import ch.logixisland.anuto.util.math.MathUtils;

public class WaveManager {

    private static final String TAG = WaveManager.class.getSimpleName();
    private static final int MAX_WAVES_IN_GAME = 5;

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final LevelLoader mLevelLoader;
    private final EnemyFactory mEnemyFactory;

    private int mNextWaveIndex;
    private int mEnemiesCount;
    private boolean mNextWaveReady;

    private final List<WaveAttender> mActiveWaves = new ArrayList<>();
    private final List<WaveListener> mListeners = new CopyOnWriteArrayList<>();

    public WaveManager(GameEngine gameEngine, ScoreBoard scoreBoard, LevelLoader levelLoader,
                       EnemyFactory enemyFactory) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mLevelLoader = levelLoader;
        mEnemyFactory = enemyFactory;

        mNextWaveIndex = 0;
        mEnemiesCount = 0;
        mNextWaveReady = true;
    }

    public int getWaveNumber() {
        return mNextWaveIndex;
    }

    public int getEnemiesCount() {
        return mEnemiesCount;
    }

    void addWaveEnemiesCount(int waveEnemiesCount) {
        mEnemiesCount += waveEnemiesCount;
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

        mActiveWaves.clear();
        mNextWaveIndex = 0;
        mNextWaveReady = true;
    }

    public void startNextWave() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    startNextWave();
                }
            });
            return;
        }

        if (!mNextWaveReady) {
            return;
        }

        if (!mActiveWaves.isEmpty()) {
            currentWave().giveWaveReward();
            mScoreBoard.giveCredits(mScoreBoard.getEarlyBonus(), false);
        }

        createAndStartWaveAttender();
        updateBonus();

        mNextWaveIndex++;
        mNextWaveReady = false;

        for (WaveListener listener : mListeners) {
            listener.waveStarted();
        }
    }

    public void addListener(WaveListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(WaveListener listener) {
        mListeners.remove(listener);
    }

    void enemyRemoved() {
        mEnemiesCount--;
        updateBonus();
    }

    void waveFinished(WaveAttender waveAttender) {
        mActiveWaves.remove(waveAttender);

        for (WaveListener listener : mListeners) {
            listener.waveFinished();
        }

        updateBonus();
        checkNextWaveReady();
    }

    void checkNextWaveReady() {
        if (mNextWaveReady) {
            return;
        }

        if (!mActiveWaves.isEmpty()) {
            WaveAttender lastWave = mActiveWaves.get(mActiveWaves.size() - 1);

            if (!lastWave.isNextWaveReady() || mActiveWaves.size() >= MAX_WAVES_IN_GAME) {
                return;
            }
        }

        mNextWaveReady = true;

        for (WaveListener listener : mListeners) {
            listener.nextWaveReady();
        }
    }

    private void updateBonus() {
        float remainingReward = 0;

        for (WaveAttender wave : mActiveWaves) {
            remainingReward += wave.getRemainingEnemiesReward();
        }

        GameSettings settings = mLevelLoader.getGameSettings();
        float modifier = settings.getEarlyModifier();
        float root = settings.getEarlyRoot();

        mScoreBoard.setEarlyBonus(Math.round(modifier * (float) Math.pow(remainingReward, 1f / root)));

        if (!mActiveWaves.isEmpty()) {
            mScoreBoard.setWaveBonus(currentWave().getWaveDescriptor().getWaveReward());
        } else {
            mScoreBoard.setWaveBonus(0);
        }
    }

    private void createAndStartWaveAttender() {
        List<WaveDescriptor> waveDescriptors = mLevelLoader.getLevelDescriptor().getWaves();
        WaveDescriptor nextWaveDescriptor = waveDescriptors.get(mNextWaveIndex % waveDescriptors.size());

        int extend = mNextWaveIndex / waveDescriptors.size() * nextWaveDescriptor.getExtend();
        if (nextWaveDescriptor.getMaxExtend() > 0 && extend > nextWaveDescriptor.getMaxExtend()) {
            extend = nextWaveDescriptor.getMaxExtend();
        }

        WaveAttender nextWave = new WaveAttender(mGameEngine, mScoreBoard, mEnemyFactory, this, nextWaveDescriptor);
        nextWave.setExtend(extend);
        updateWaveModifiers(nextWave);
        mActiveWaves.add(nextWave);
        nextWave.start();
    }

    private void updateWaveModifiers(WaveAttender wave) {
        Log.d(TAG, String.format("calculating wave modifiers for wave %d...", getWaveNumber() + 1));
        Log.d(TAG, String.format("creditsEarned=%d", mScoreBoard.getCreditsEarned()));

        float waveHealth = 0f;
        for (EnemyDescriptor d : wave.getWaveDescriptor().getEnemies()) {
            waveHealth += mLevelLoader.getEnemySettings().getEnemyConfig(d.getName()).getHealth();
        }

        waveHealth *= wave.getExtend() + 1;

        Log.d(TAG, String.format("waveHealth=%f", waveHealth));

        GameSettings settings = mLevelLoader.getGameSettings();
        float damagePossible = settings.getDifficultyOffset()
                + settings.getDifficultyLinear() * mScoreBoard.getCreditsEarned()
                + settings.getDifficultyQuadratic() * MathUtils.square(mScoreBoard.getCreditsEarned());
        float healthModifier = damagePossible / waveHealth;

        wave.modifyEnemyHealth(healthModifier);

        float rewardModifier = settings.getRewardModifier()
                * (float) Math.pow(wave.getEnemyHealthModifier(), 1f / settings.getRewardRoot());

        if (rewardModifier < 1f) {
            rewardModifier = 1f;
        }

        wave.modifyEnemyReward(rewardModifier);
        wave.modifyWaveReward((getWaveNumber() / mLevelLoader.getLevelDescriptor().getWaves().size()) + 1);

        Log.d(TAG, String.format("waveNumber=%d", getWaveNumber()));
        Log.d(TAG, String.format("damagePossible=%f\n", damagePossible));
        Log.d(TAG, String.format("healthModifier=%f", wave.getEnemyHealthModifier()));
        Log.d(TAG, String.format("rewardModifier=%f", wave.getEnemyRewardModifier()));
    }

    private WaveAttender currentWave() {
        if (mActiveWaves.isEmpty()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }
}
