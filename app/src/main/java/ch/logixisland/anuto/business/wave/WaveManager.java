package ch.logixisland.anuto.business.wave;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.GameStateListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.business.tower.TowerAging;
import ch.logixisland.anuto.data.setting.GameSettingsRoot;
import ch.logixisland.anuto.data.wave.EnemyDescriptor;
import ch.logixisland.anuto.data.wave.WaveDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;

public class WaveManager implements GameStateListener {

    private static final String TAG = WaveManager.class.getSimpleName();

    private static final int MAX_WAVES_IN_GAME = 3;
    private static final float MIN_WAVE_DELAY = 5;

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final GameState mGameState;
    private final TowerAging mTowerAging;
    private final EntityRegistry mEntityRegistry;

    private final EnemyDefaultHealth mEnemyDefaultHealth;

    private int mNextWaveIndex;
    private int mRemainingEnemiesCount;
    private boolean mNextWaveReady;
    private boolean mMinWaveDelayTimeout;

    private final List<WaveAttender> mActiveWaves = new ArrayList<>();
    private final List<WaveListener> mListeners = new CopyOnWriteArrayList<>();

    public WaveManager(GameEngine gameEngine, ScoreBoard scoreBoard, GameState gameState,
                       EntityRegistry entityRegistry, TowerAging towerAging) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mGameState = gameState;
        mTowerAging = towerAging;
        mEntityRegistry = entityRegistry;

        mEnemyDefaultHealth = new EnemyDefaultHealth(entityRegistry);

        gameState.addListener(this);
    }

    public int getWaveNumber() {
        return mNextWaveIndex;
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

        mGameState.setGameStarted();

        giveWaveRewardAndEarlyBonus();
        createAndStartWaveAttender();
        updateBonusOnScoreBoard();
        updateRemainingEnemiesCount();

        incrementNextWaveIndex();
        setNextWaveReady(false);
        triggerMinWaveDelay();
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

        resetNextWaveIndex();
        setNextWaveReady(true);
        updateRemainingEnemiesCount();
    }

    @Override
    public void gameOver() {

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
        if (!mActiveWaves.isEmpty()) {
            getCurrentWave().giveWaveReward();
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

        if (!mActiveWaves.isEmpty()) {
            mScoreBoard.setWaveBonus(getCurrentWave().getWaveReward());
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

            for (WaveListener listener : mListeners) {
                listener.remainingEnemiesCountChanged();
            }
        }
    }

    private void createAndStartWaveAttender() {
        List<WaveDescriptor> waveDescriptors = mGameEngine.getGameConfiguration().getWaveDescriptorRoot().getWaves();
        WaveDescriptor nextWaveDescriptor = waveDescriptors.get(mNextWaveIndex % waveDescriptors.size());
        WaveAttender nextWave = new WaveAttender(mGameEngine, mScoreBoard, mEntityRegistry, this, nextWaveDescriptor);
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
        GameSettingsRoot settings = mGameEngine.getGameConfiguration().getGameSettingsRoot();

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
            waveHealth += mEnemyDefaultHealth.getDefaultHealth(d.getName());
        }
        waveHealth *= wave.getExtend() + 1;
        return waveHealth;
    }

    private int getIterationNumber() {
        return (getWaveNumber() / mGameEngine.getGameConfiguration().getWaveDescriptorRoot().getWaves().size()) + 1;
    }

    private int getEarlyBonus() {
        float remainingReward = 0;

        for (WaveAttender wave : mActiveWaves) {
            remainingReward += wave.getRemainingEnemiesReward();
        }

        GameSettingsRoot settings = mGameEngine.getGameConfiguration().getGameSettingsRoot();
        return Math.round(settings.getEarlyModifier() * (float) Math.pow(remainingReward, settings.getEarlyExponent()));
    }

    private WaveAttender getCurrentWave() {
        if (mActiveWaves.isEmpty()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }

    private void resetNextWaveIndex() {
        if (mNextWaveIndex != 0) {
            mNextWaveIndex = 0;

            for (WaveListener listener : mListeners) {
                listener.waveNumberChanged();
            }
        }
    }

    private void incrementNextWaveIndex() {
        mNextWaveIndex++;

        for (WaveListener listener : mListeners) {
            listener.waveNumberChanged();
        }
    }

    private void setNextWaveReady(boolean ready) {
        if (mNextWaveReady != ready) {
            mNextWaveReady = ready;

            for (WaveListener listener : mListeners) {
                listener.nextWaveReadyChanged();
            }
        }
    }
}
