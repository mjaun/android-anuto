package ch.logixisland.anuto.business.level;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.util.data.EnemyDescriptor;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.WaveDescriptor;

public class WaveManager implements GameListener {

    private static final String TAG = WaveManager.class.getSimpleName();

    private static final int MAX_WAVES_IN_GAME = 3;
    private static final float MIN_WAVE_DELAY = 5;

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;
    private final EnemyFactory mEnemyFactory;

    private int mNextWaveIndex;
    private boolean mNextWaveReady;
    private boolean mMinWaveDelayTimeout;

    private final List<WaveAttender> mActiveWaves = new ArrayList<>();
    private final List<WaveListener> mListeners = new CopyOnWriteArrayList<>();

    public WaveManager(GameEngine gameEngine, ScoreBoard scoreBoard, GameManager gameManager, LevelLoader levelLoader,
                       EnemyFactory enemyFactory) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mGameManager = gameManager;
        mLevelLoader = levelLoader;
        mEnemyFactory = enemyFactory;

        mNextWaveIndex = 0;
        mNextWaveReady = true;

        gameManager.addListener(this);
    }

    public int getWaveNumber() {
        return mNextWaveIndex;
    }

    public int getRemainingEnemiesCount() {
        int totalCount = 0;

        for (WaveAttender waveAttender : mActiveWaves) {
            totalCount += waveAttender.getRemainingEnemiesCount();
        }

        return totalCount;
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

        mGameManager.setGameStarted();

        giveWaveRewardAndEarlyBonus();
        createAndStartWaveAttender();
        updateBonusOnScoreBoard();

        mNextWaveIndex++;
        mNextWaveReady = false;
        mMinWaveDelayTimeout = false;

        for (WaveListener listener : mListeners) {
            listener.waveStarted();
        }

        mGameEngine.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMinWaveDelayTimeout = true;
                checkNextWaveReady();
            }
        }, MIN_WAVE_DELAY);
    }

    public void addListener(WaveListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(WaveListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void gameRestart() {
        mActiveWaves.clear();
        mNextWaveIndex = 0;
        mNextWaveReady = true;
        mMinWaveDelayTimeout = true;
    }

    @Override
    public void gameOver() {

    }

    void enemyRemoved() {
        updateBonusOnScoreBoard();

        for (WaveListener listener : mListeners) {
            listener.enemyRemoved();
        }
    }

    void waveFinished(WaveAttender waveAttender) {
        mActiveWaves.remove(waveAttender);

        for (WaveListener listener : mListeners) {
            listener.waveFinished();
        }

        updateBonusOnScoreBoard();
        checkNextWaveReady();
    }

    private void giveWaveRewardAndEarlyBonus() {
        if (!mActiveWaves.isEmpty()) {
            getCurrentWave().giveWaveReward();
            mScoreBoard.giveCredits(getEarlyBonus(), false);
        }
    }

    private void checkNextWaveReady() {
        if (mNextWaveReady) {
            return;
        }

        if (!mMinWaveDelayTimeout) {
            return;
        }

        if (mActiveWaves.size() >= MAX_WAVES_IN_GAME) {
            return;
        }

        mNextWaveReady = true;

        for (WaveListener listener : mListeners) {
            listener.nextWaveReady();
        }
    }

    private void updateBonusOnScoreBoard() {
        mScoreBoard.setEarlyBonus(getEarlyBonus());

        if (!mActiveWaves.isEmpty()) {
            mScoreBoard.setWaveBonus(getCurrentWave().getWaveReward());
        } else {
            mScoreBoard.setWaveBonus(0);
        }
    }

    private void createAndStartWaveAttender() {
        List<WaveDescriptor> waveDescriptors = mLevelLoader.getWavesDescriptor().getWaves();
        WaveDescriptor nextWaveDescriptor = waveDescriptors.get(mNextWaveIndex % waveDescriptors.size());
        WaveAttender nextWave = new WaveAttender(mGameEngine, mScoreBoard, mLevelLoader, mEnemyFactory, this, nextWaveDescriptor);
        updateWaveExtend(nextWave, nextWaveDescriptor);
        updateWaveModifiers(nextWave);
        mActiveWaves.add(nextWave);
        nextWave.start();
    }

    private void updateWaveExtend(WaveAttender wave, WaveDescriptor waveDescriptor) {
        int extend = Math.min((getIterationNumber() - 1) * waveDescriptor.getExtend(), waveDescriptor.getMaxExtend());
        wave.setExtend(extend);
    }

    private void updateWaveModifiers(WaveAttender wave) {
        GameSettings settings = mLevelLoader.getGameSettings();

        float waveHealth = getWaveHealth(wave);
        float damagePossible = settings.getDifficultyLinear() * mScoreBoard.getCreditsEarned()
                + settings.getDifficultyModifier() * (float) Math.pow(mScoreBoard.getCreditsEarned(), settings.getDifficultyExponent());
        float healthModifier = damagePossible / waveHealth;
        healthModifier = Math.max(healthModifier, settings.getMinHealthModifier());

        float rewardModifier = settings.getRewardModifier() * (float) Math.pow(healthModifier, settings.getRewardExponent());
        rewardModifier = Math.max(rewardModifier, settings.getMinRewardModifier());

        wave.modifyEnemyHealth(healthModifier);
        wave.modifyEnemyReward(rewardModifier);
        wave.modifyWaveReward(getIterationNumber());

        Log.i(TAG, String.format("waveNumber=%d", getWaveNumber()));
        Log.i(TAG, String.format("waveHealth=%f", waveHealth));
        Log.i(TAG, String.format("creditsEarned=%d", mScoreBoard.getCreditsEarned()));
        Log.i(TAG, String.format("damagePossible=%f", damagePossible));
        Log.i(TAG, String.format("healthModifier=%f", wave.getEnemyHealthModifier()));
        Log.i(TAG, String.format("rewardModifier=%f", wave.getEnemyRewardModifier()));
    }

    private float getWaveHealth(WaveAttender wave) {
        float waveHealth = 0f;
        for (EnemyDescriptor d : wave.getWaveDescriptor().getEnemies()) {
            waveHealth += mLevelLoader.getEnemySettings().getEnemyConfig(d.getName()).getHealth();
        }
        waveHealth *= wave.getExtend() + 1;
        return waveHealth;
    }

    private int getIterationNumber() {
        return (getWaveNumber() / mLevelLoader.getWavesDescriptor().getWaves().size()) + 1;
    }

    private int getEarlyBonus() {
        float remainingReward = 0;

        for (WaveAttender wave : mActiveWaves) {
            remainingReward += wave.getRemainingEnemiesReward();
        }

        GameSettings settings = mLevelLoader.getGameSettings();
        return Math.round(settings.getEarlyModifier() * (float) Math.pow(remainingReward, settings.getEarlyExponent()));
    }

    private WaveAttender getCurrentWave() {
        if (mActiveWaves.isEmpty()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }
}
