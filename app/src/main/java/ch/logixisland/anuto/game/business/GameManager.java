package ch.logixisland.anuto.game.business;

import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.data.EnemyDescriptor;
import ch.logixisland.anuto.game.data.Level;
import ch.logixisland.anuto.game.data.PlateauDescriptor;
import ch.logixisland.anuto.game.data.Settings;
import ch.logixisland.anuto.game.data.Wave;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.entity.plateau.Plateau;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.render.Viewport;
import ch.logixisland.anuto.util.container.ListenerList;
import ch.logixisland.anuto.util.math.MathUtils;

public class GameManager {

    /*
    ------ Constants ------
     */

    private final static String TAG = GameManager.class.getSimpleName();

    /*
    ------ Listener Interface ------
     */

    public interface Listener {

    }

    public interface OnGameStartedListener extends Listener {
        void onGameStarted();
    }

    public interface OnGameOverListener extends Listener {
        void onGameOver();
    }

    public interface OnWaveStartedListener extends Listener {
        void onWaveStarted(Wave wave);
    }

    public interface OnNextWaveReadyListener extends Listener {
        void onNextWaveReady(Wave wave);
    }

    public interface OnWaveDoneListener extends Listener {
        void onWaveDone(Wave wave);
    }

    public interface OnCreditsChangedListener extends Listener {
        void onCreditsChanged(int credits);
    }

    public interface OnBonusChangedListener extends Listener {
        void onBonusChanged(int bonus, int earlyBonus);
    }

    public interface OnLivesChangedListener extends Listener {
        void onLivesChanged(int lives);
    }

    public interface OnShowTowerInfoListener extends Listener {
        void onShowTowerInfo(Tower tower);
    }

    public interface OnHideTowerInfoListener extends Listener {
        void onHideTowerInfo();
    }

    public interface OnTowersAgedListener extends Listener {
        void onTowersAged();
    }

    /*
    ------ Members ------
     */

    private final GameEngine mGameEngine;
    private final Viewport mViewport;

    private Level mLevel;
    private Tower mSelectedTower;

    private int mNextWaveIndex;

    private volatile int mCredits;
    private volatile int mCreditsEarned;
    private volatile int mScore;
    private volatile int mLives;
    private volatile int mEarlyBonus;
    private volatile boolean mGameOver;
    private volatile boolean mGameWon;
    private volatile boolean mNextWaveReady;

    private List<WaveManager> mActiveWaves = new CopyOnWriteArrayList<>();

    private ListenerList<Listener> mListeners = new ListenerList<>();

    /*
    ------ WaveManager.Listener Implementation ------
     */

    private WaveManager.Listener mWaveListener = new WaveManager.Listener() {
        @Override
        public void onStarted(final WaveManager m) {
            mActiveWaves.add(m);
            calcEarlyBonus();

            mGameEngine.add(new Runnable() {
                private final TickTimer mTimer = TickTimer.createInterval(m.getWave().getNextWaveDelay());

                @Override
                public void run() {
                    if (mTimer.tick()) {
                        if (getCurrentWaveManager() == m && !mNextWaveReady && hasNextWave()) {
                            onNextWaveReady();
                            mNextWaveReady = true;
                        }

                        mGameEngine.remove(this);
                    }
                }
            });

            onWaveStarted(m.getWave());
        }

        @Override
        public void onAborted(WaveManager m) {
        }

        @Override
        public void onFinished(WaveManager m) {
            if (getCurrentWaveManager() == m && !mNextWaveReady && hasNextWave()) {
                onNextWaveReady();
                mNextWaveReady = true;
            }

            mActiveWaves.remove(m);
            m.giveReward();

            ageTowers();
            onWaveDone(m.getWave());

            if (!hasNextWave()) {
                mGameOver = true;
                mGameWon = true;
                onGameOver();
            }
        }

        @Override
        public void onEnemyAdded(WaveManager m, Enemy e) {
            mActiveWaves.remove(m);
        }

        @Override
        public void onEnemyRemoved(WaveManager m, Enemy e) {
            calcEarlyBonus();
        }
    };

    /*
    ------ Constructors ------
     */

    public GameManager(GameEngine gameEngine, Viewport viewport) {
        mGameEngine = gameEngine;
        mViewport = viewport;
        mGameOver = true;
    }

    /*
    ------ Methods ------
     */

    private void reset() {
        for (WaveManager m : mActiveWaves) {
            m.abort();
        }

        mActiveWaves.clear();
        mGameEngine.clear();

        mSelectedTower = null;
        mNextWaveIndex = 0;
        mGameOver = false;

        hideTowerInfo();
    }

    public void restart() {
        reset();

        for (PlateauDescriptor d : mLevel.getPlateaus()) {
            Plateau p = d.createInstance();
            p.setPosition(d.getX(), d.getY());
            mGameEngine.add(p);
        }

        mViewport.setGameSize(getSettings().getWidth(), getSettings().getHeight());

        mEarlyBonus = 0;
        mNextWaveReady = true;
        mCreditsEarned = getSettings().getCredits();
        mScore = 0;

        onGameStarted();

        setCredits(getSettings().getCredits());
        setLives(getSettings().getLives());

        onBonusChanged();
    }


    public Level getLevel() {
        return mLevel;
    }

    public void setLevel(Level level) {
        mLevel = level;

        if (mLevel == null) {
            reset();
        } else {
            restart();
        }
    }

    public Settings getSettings() {
        return mLevel.getSettings();
    }


    public int getWaveNumber() {
        return mNextWaveIndex;
    }

    public boolean hasCurrentWave() {
        return !mActiveWaves.isEmpty();
    }

    public boolean hasNextWave() {
        if (getSettings().isEndless() && !mLevel.getWaves().isEmpty()) {
            return true;
        }

        return mNextWaveIndex < mLevel.getWaves().size();
    }

    public Wave getCurrentWave() {
        if (!hasCurrentWave()) {
            return null;
        }

        return getCurrentWaveManager().getWave();
    }

    private WaveManager getCurrentWaveManager() {
        if (!hasCurrentWave()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }

    public Wave getNextWave() {
        if (!hasNextWave()) {
            return null;
        }

        return mLevel.getWaves().get(mNextWaveIndex % mLevel.getWaves().size());
    }

    public int getWaveIterationCount() {
        return mNextWaveIndex / mLevel.getWaves().size();
    }

    public void startNextWave() {
        if (hasCurrentWave()) {
            getCurrentWaveManager().giveReward();
            giveCredits(mEarlyBonus, false);
        }

        Wave nextWave = getNextWave();
        int extend = nextWave.getExtend() * getWaveIterationCount();

        if (nextWave.getMaxExtend() > 0 && extend > nextWave.getMaxExtend()) {
            extend = nextWave.getMaxExtend();
        }

        WaveManager m = new WaveManager(mGameEngine, this, nextWave, extend);
        m.addListener(mWaveListener);

        if (getSettings().isEndless()) {
            calcWaveModifiers(m);
        }

        m.start();

        mNextWaveIndex++;
        mNextWaveReady = false;
    }


    public int getScore() {
        return mScore;
    }

    public int getCredits() {
        return mCredits;
    }

    public void setCredits(int credits) {
        mCredits = credits;
        onCreditsChanged();
    }

    public void giveCredits(int amount, boolean earned) {
        if (amount <= 0) {
            return;
        }

        mCredits += amount;

        if (!mGameOver) {
            mScore += amount;
        }

        if (earned) {
            mCreditsEarned += amount;
        }

        onCreditsChanged();
    }

    public void takeCredits(int amount) {
        if (amount <= 0) {
            return;
        }

        mCredits -= amount;
        onCreditsChanged();
    }

    public int getBonus() {
        if (!hasCurrentWave()) {
            return 0;
        }

        return getCurrentWaveManager().getReward();
    }

    public int getEarlyBonus() {
        return mEarlyBonus;
    }


    public int getLives() {
        return mLives;
    }

    public void setLives(int lives) {
        mLives = lives;
        onLivesChanged();
    }

    public void giveLives(int count) {
        if (!isGameOver()) {
            mLives += count;
            onLivesChanged();
        }
    }

    public void takeLives(int count) {
        mLives -= count;

        onLivesChanged();

        if (mLives < 0 && !mGameOver) {
            mGameOver = true;
            mGameWon = false;
            onGameOver();
        }
    }


    public boolean isGameOver() {
        return mGameOver;
    }

    public boolean isGameWon() {
        return mGameWon;
    }


    public Tower getSelectedTower() {
        return mSelectedTower;
    }

    public void setSelectedTower(Tower tower) {
        if (mSelectedTower != null) {
            mSelectedTower.hideRange();
        }

        mSelectedTower = tower;

        if (mSelectedTower != null) {
            mSelectedTower.showRange();
        }
    }

    public void showTowerInfo(Tower tower) {
        onShowTowerInfo(tower);
    }

    public void hideTowerInfo() {
        onHideTowerInfo();
    }


    private void ageTowers() {
        Iterator<Tower> it = mGameEngine.get(Types.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower t = it.next();
            t.devalue(getSettings().getAgeModifier());
        }

        onTowersAged();
    }

    private void calcEarlyBonus() {
        float earlyBonus = 0;

        for (WaveManager m : mActiveWaves) {
            earlyBonus += m.getEarlyBonus();
        }

        float modifier = getSettings().getEarlyModifier();
        float root = getSettings().getEarlyRoot();

        mEarlyBonus = Math.round(modifier * (float)Math.pow(earlyBonus, 1f / root));
        onBonusChanged();
    }

    private void calcWaveModifiers(WaveManager waveMan) {
        Log.d(TAG, String.format("calculating wave modifiers for wave %d...", getWaveNumber() + 1));
        Log.d(TAG, String.format("creditsEarned=%d", mCreditsEarned));

        float waveHealth = 0f;

        for (EnemyDescriptor d : waveMan.getWave().getEnemies()) {
            waveHealth += mLevel.getEnemyConfig(d.getEnemyClass()).getHealth();
        }

        waveHealth *= waveMan.getExtend() + 1;

        Log.d(TAG, String.format("waveHealth=%f", waveHealth));

        float damagePossible = getSettings().getDifficultyOffset()
                + getSettings().getDifficultyLinear() * mCreditsEarned
                + getSettings().getDifficultyQuadratic() * MathUtils.square(mCreditsEarned);
        float healthModifier = damagePossible / waveHealth;

        waveMan.modifyHealth(healthModifier);

        float rewardModifier = getSettings().getRewardModifier()
                * (float)Math.pow(waveMan.getHealthModifier(), 1f / getSettings().getRewardRoot());

        if (rewardModifier < 1f) {
            rewardModifier = 1f;
        }

        waveMan.modifyReward(rewardModifier);
        waveMan.modifyWaveReward((getWaveNumber() / mLevel.getWaves().size()) + 1);

        Log.d(TAG, String.format("waveNumber=%d", getWaveNumber()));
        Log.d(TAG, String.format("damagePossible=%f\n", damagePossible));
        Log.d(TAG, String.format("healthModifier=%f", waveMan.getHealthModifier()));
        Log.d(TAG, String.format("rewardModifier=%f", waveMan.getRewardModifier()));
    }

    /*
    ------ Listener Stuff ------
     */

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }


    private void onGameStarted() {
        Log.i(TAG, "Game started.");

        for (OnGameStartedListener l : mListeners.get(OnGameStartedListener.class)) {
            l.onGameStarted();
        }
    }

    private void onGameOver() {
        Log.i(TAG, "Game over.");

        for (OnGameOverListener l : mListeners.get(OnGameOverListener.class)) {
            l.onGameOver();
        }
    }

    private void onCreditsChanged() {
        for (OnCreditsChangedListener l : mListeners.get(OnCreditsChangedListener.class)) {
            l.onCreditsChanged(getCredits());
        }
    }

    private void onBonusChanged() {
        for (OnBonusChangedListener l : mListeners.get(OnBonusChangedListener.class)) {
            l.onBonusChanged(getBonus(), getEarlyBonus());
        }
    }

    private void onLivesChanged() {
        for (OnLivesChangedListener l : mListeners.get(OnLivesChangedListener.class)) {
            l.onLivesChanged(getLives());
        }
    }

    public void onWaveStarted(Wave wave) {
        Log.i(TAG, "Wave started.");

        for (OnWaveStartedListener l : mListeners.get(OnWaveStartedListener.class)) {
            l.onWaveStarted(wave);
        }
    }

    private void onNextWaveReady() {
        Log.i(TAG, "Next wave ready.");

        for (OnNextWaveReadyListener l : mListeners.get(OnNextWaveReadyListener.class)) {
            l.onNextWaveReady(getNextWave());
        }
    }

    public void onWaveDone(Wave wave) {
        Log.i(TAG, "Wave done.");

        for (OnWaveDoneListener l : mListeners.get(OnWaveDoneListener.class)) {
            l.onWaveDone(wave);
        }
    }

    private void onShowTowerInfo(Tower tower) {
        for (OnShowTowerInfoListener l : mListeners.get(OnShowTowerInfoListener.class)) {
            l.onShowTowerInfo(tower);
        }
    }

    private void onHideTowerInfo() {
        for (OnHideTowerInfoListener l : mListeners.get(OnHideTowerInfoListener.class)) {
            l.onHideTowerInfo();
        }
    }

    private void onTowersAged() {
        for (OnTowersAgedListener l : mListeners.get(OnTowersAgedListener.class)) {
            l.onTowersAged();
        }
    }
}
