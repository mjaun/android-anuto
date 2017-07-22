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
import ch.logixisland.anuto.util.math.MathUtils;

public class WaveManager implements GameListener {

    private static final String TAG = WaveManager.class.getSimpleName();
    private static final int MAX_WAVES_IN_GAME = 5;

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;
    private final EnemyFactory mEnemyFactory;

    private int mNextWaveIndex;
    private boolean mNextWaveReady;

    private final List<EnemyInserter> mActiveWaves = new ArrayList<>();
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

        for (EnemyInserter enemyInserter : mActiveWaves) {
            totalCount += enemyInserter.getRemainingEnemiesCount();
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

        if (!mActiveWaves.isEmpty()) {
            currentWave().giveWaveReward();
            mScoreBoard.giveCredits(mScoreBoard.getEarlyBonus(), false);
        }

        createAndStartEnemyInserter();
        updateBonus();

        mNextWaveIndex++;
        mNextWaveReady = false;

        for (WaveListener listener : mListeners) {
            listener.waveStarted();
        }

        checkNextWaveReady();
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
    }

    @Override
    public void gameOver() {

    }

    void enemyRemoved() {
        updateBonus();

        for (WaveListener listener : mListeners) {
            listener.enemyRemoved();
        }
    }

    void waveFinished(EnemyInserter enemyInserter) {
        mActiveWaves.remove(enemyInserter);

        for (WaveListener listener : mListeners) {
            listener.waveFinished();
        }

        updateBonus();
        checkNextWaveReady();
    }

    private void checkNextWaveReady() {
        if (mNextWaveReady) {
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

    private void updateBonus() {
        float remainingReward = 0;

        for (EnemyInserter wave : mActiveWaves) {
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

    private void createAndStartEnemyInserter() {
        List<WaveDescriptor> waveDescriptors = mLevelLoader.getWavesDescriptor().getWaves();
        WaveDescriptor nextWaveDescriptor = waveDescriptors.get(mNextWaveIndex % waveDescriptors.size());

        int extend = mNextWaveIndex / waveDescriptors.size() * nextWaveDescriptor.getExtend();
        if (nextWaveDescriptor.getMaxExtend() > 0 && extend > nextWaveDescriptor.getMaxExtend()) {
            extend = nextWaveDescriptor.getMaxExtend();
        }

        EnemyInserter nextWave = new EnemyInserter(mGameEngine, mScoreBoard, mEnemyFactory, this, nextWaveDescriptor, extend);
        updateWaveModifiers(nextWave);
        mActiveWaves.add(nextWave);
        nextWave.start();
    }

    private void updateWaveModifiers(EnemyInserter wave) {
        float waveHealth = 0f;
        for (EnemyDescriptor d : wave.getWaveDescriptor().getEnemies()) {
            waveHealth += mLevelLoader.getEnemySettings().getEnemyConfig(d.getName()).getHealth();
        }
        waveHealth *= wave.getExtend() + 1;

        GameSettings settings = mLevelLoader.getGameSettings();
        float damagePossible = settings.getDifficultyOffset()
                + settings.getDifficultyLinear() * mScoreBoard.getCreditsEarned()
                + settings.getDifficultyQuadratic() * MathUtils.square(mScoreBoard.getCreditsEarned());
        float healthModifier = Math.max(damagePossible / waveHealth, settings.getMinHealthModifier());

        float rewardModifier = settings.getRewardModifier()
                * (float) Math.pow(wave.getEnemyHealthModifier(), 1f / settings.getRewardRoot());

        if (rewardModifier < 1f) {
            rewardModifier = 1f;
        }

        wave.modifyEnemyHealth(healthModifier);
        wave.modifyEnemyReward(rewardModifier);
        wave.modifyWaveReward((getWaveNumber() / mLevelLoader.getWavesDescriptor().getWaves().size()) + 1);

        Log.d(TAG, String.format("waveNumber=%d", getWaveNumber()));
        Log.d(TAG, String.format("waveHealth=%f", waveHealth));
        Log.d(TAG, String.format("creditsEarned=%d", mScoreBoard.getCreditsEarned()));
        Log.d(TAG, String.format("damagePossible=%f", damagePossible));
        Log.d(TAG, String.format("healthModifier=%f", wave.getEnemyHealthModifier()));
        Log.d(TAG, String.format("rewardModifier=%f", wave.getEnemyRewardModifier()));
    }

    private EnemyInserter currentWave() {
        if (mActiveWaves.isEmpty()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }
}
