package ch.logixisland.anuto.game;

import android.os.Handler;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.data.EnemyDescriptor;
import ch.logixisland.anuto.game.data.Level;
import ch.logixisland.anuto.game.data.PlateauDescriptor;
import ch.logixisland.anuto.game.data.Settings;
import ch.logixisland.anuto.game.data.Wave;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.container.ListenerList;

public class GameManager {

    /*
    ------ Static ------
     */

    private static GameManager sInstance;

    public static GameManager getInstance() {
        if (sInstance == null) {
            sInstance = new GameManager();
        }

        return sInstance;
    }

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
        void onBonusChanged(int bonus);
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

    /*
    ------ WaveManager Class ------
     */

    private class WaveManager implements GameObject.Listener {

        private Wave mWave;
        private Handler mHandler;
        private boolean mAborted;
        private int mEarlyBonus;
        private int mWaveReward;
        private int mEnemiesInQueue;
        private List<Enemy> mEnemiesInGame = new CopyOnWriteArrayList<>();

        public WaveManager(Wave wave) {
            mWave = wave;
            mHandler = mGame.createHandler();
        }

        public void start() {
            int delay = 0;

            mEarlyBonus = 0;
            mWaveReward = mWave.waveReward;
            mAborted = false;
            mActiveWaves.add(this);

            for (EnemyDescriptor d : mWave.enemies) {
                final Enemy e = d.create();
                e.addListener(this);

                delay += (int)(d.delay * 1000f);
                mEarlyBonus += e.getReward();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGame.add(e);
                    }
                }, delay);

                mEnemiesInQueue++;
            }

            ageTowers();
            onWaveStarted(mWave);

            calcEarlyBonus();
        }

        public void abort() {
            mHandler.removeCallbacksAndMessages(null);
            mEnemiesInQueue = 0;
            mAborted = true;
        }

        @Override
        public void onObjectAdded(GameObject obj) {
            mEnemiesInQueue--;
            mEnemiesInGame.add((Enemy)obj);

            if (mEnemiesInQueue == 0 && hasNextWave() && !mAborted) {
                onNextWaveReady();
            }
        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            mEnemiesInGame.remove(obj);
            mEarlyBonus -= ((Enemy)obj).getReward();
            calcEarlyBonus();

            if (mEnemiesInQueue == 0 && mEnemiesInGame.isEmpty() && !mAborted) {
                mActiveWaves.remove(this);

                onWaveDone(mWave);
                giveCredits(mWaveReward);

                if (!hasNextWave()) {
                    mGameOver = true;
                    mGameWon = true;
                    onGameOver();
                }
            }
        }
    }

    /*
    ------ Members ------
     */

    private GameEngine mGame;
    private Level mLevel;

    private int mNextWaveIndex;
    private int mCredits;
    private int mLives;
    private int mEarlyBonus;
    private boolean mGameOver;
    private boolean mGameWon;
    private Tower mSelectedTower;

    private List<WaveManager> mActiveWaves = new CopyOnWriteArrayList<>();

    private ListenerList<Listener> mListeners = new ListenerList<>();

    /*
    ------ Constructors ------
     */

    public GameManager() {
        mGame = GameEngine.getInstance();
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
        mGame.clear();

        mSelectedTower = null;
        mNextWaveIndex = 0;
        mGameOver = false;

        hideTowerInfo();
    }

    public void restart() {
        reset();

        for (PlateauDescriptor d : mLevel.getPlateaus()) {
            mGame.add(d.create());
        }

        Settings settings = mLevel.getSettings();
        mGame.setGameSize(settings.width, settings.height);

        onGameStarted();

        setCredits(settings.credits);
        setLives(settings.lives);

        mEarlyBonus = 0;
        onEarlyBonusChanged();
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


    public int getWaveNumber() {
        return mNextWaveIndex;
    }

    public boolean hasCurrentWave() {
        return !mActiveWaves.isEmpty();
    }

    public boolean hasNextWave() {
        return mNextWaveIndex < mLevel.getWaves().size();
    }

    public Wave getCurrentWave() {
        if (!hasCurrentWave()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1).mWave;
    }

    public Wave getNextWave() {
        if (!hasNextWave()) {
            return null;
        }

        return mLevel.getWaves().get(mNextWaveIndex);
    }

    public void startNextWave() {
        if (hasCurrentWave()) {
            WaveManager m = mActiveWaves.get(mActiveWaves.size() - 1);
            giveCredits(m.mWaveReward);
            m.mWaveReward = 0;

            giveCredits(mEarlyBonus);
        }

        new WaveManager(getNextWave()).start();
        mNextWaveIndex++;
    }


    public int getCredits() {
        return mCredits;
    }

    public void setCredits(int credits) {
        mCredits = credits;
        onCreditsChanged();
    }

    public void giveCredits(int amount) {
        mCredits += amount;
        onCreditsChanged();
    }

    public void takeCredits(int amount) {
        mCredits -= amount;
        onCreditsChanged();
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
        Iterator<Tower> it = mGame.get(TypeIds.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower t = it.next();
            t.devalue(mLevel.getSettings().agingFactor);
        }
    }

    private void calcEarlyBonus() {
        mEarlyBonus = 0;

        for (WaveManager m : mActiveWaves) {
            mEarlyBonus += m.mEarlyBonus;
        }

        mEarlyBonus = Math.round(mLevel.getSettings().earlyFactor * mEarlyBonus);
        onEarlyBonusChanged();
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
        for (OnGameStartedListener l : mListeners.get(OnGameStartedListener.class)) {
            l.onGameStarted();
        }
    }

    private void onGameOver() {
        for (OnGameOverListener l : mListeners.get(OnGameOverListener.class)) {
            l.onGameOver();
        }
    }

    private void onCreditsChanged() {
        for (OnCreditsChangedListener l : mListeners.get(OnCreditsChangedListener.class)) {
            l.onCreditsChanged(getCredits());
        }
    }

    private void onEarlyBonusChanged() {
        for (OnBonusChangedListener l : mListeners.get(OnBonusChangedListener.class)) {
            l.onBonusChanged(getEarlyBonus());
        }
    }

    private void onLivesChanged() {
        for (OnLivesChangedListener l : mListeners.get(OnLivesChangedListener.class)) {
            l.onLivesChanged(getLives());
        }
    }

    public void onWaveStarted(Wave wave) {
        for (OnWaveStartedListener l : mListeners.get(OnWaveStartedListener.class)) {
            l.onWaveStarted(wave);
        }
    }

    private void onNextWaveReady() {
        for (OnNextWaveReadyListener l : mListeners.get(OnNextWaveReadyListener.class)) {
            l.onNextWaveReady(getNextWave());
        }
    }

    public void onWaveDone(Wave wave) {
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
}
