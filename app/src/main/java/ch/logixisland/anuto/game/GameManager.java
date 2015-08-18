package ch.logixisland.anuto.game;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.data.GameSettings;
import ch.logixisland.anuto.game.data.Level;
import ch.logixisland.anuto.game.data.Wave;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Plateau;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.container.ListenerList;

public class GameManager implements Wave.Listener {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {

    }

    public interface OnGameStartedListener extends Listener {
        void onGameStarted();
    }

    public interface OnGameOverListener extends Listener {
        void onGameOver(boolean won);
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
    ------ Members ------
     */

    private Level mLevel;

    private int mNextWaveIndex;
    private int mCredits;
    private int mLives;
    private boolean mGameOver = true;

    private Tower mSelectedTower;

    private final GameEngine mGame;
    private final List<Wave> mActiveWaves = new CopyOnWriteArrayList<>();

    private final ListenerList<Listener> mListeners = new ListenerList<>();

    /*
    ------ Constructors ------
     */

    public GameManager() {
        mGame = GameEngine.getInstance();
    }

    /*
    ------ Methods ------
     */

    private void reset() {
        mLevel = null;

        for (Wave w : mActiveWaves) {
            w.removeListener(this);
            w.abort();
        }

        mActiveWaves.clear();
        mGame.clear();

        mSelectedTower = null;
        mNextWaveIndex = 0;
        mGameOver = false;
    }

    public void restart() {
        setLevel(mLevel);
    }


    public Level getLevel() {
        return mLevel;
    }

    public void setLevel(Level level) {
        reset();
        mLevel = level;

        for (Plateau p : level.getPlateaus()) {
            mGame.add(p);
        }

        GameSettings settings = mLevel.getSettings();
        mGame.setGameSize(settings.width, settings.height);

        onGameStarted();
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

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }

    public Wave getNextWave() {
        if (!hasNextWave()) {
            return null;
        }

        return mLevel.getWaves().get(mNextWaveIndex);
    }

    public void startNextWave() {
        Wave wave = getNextWave();
        wave.addListener(this);
        wave.start();
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
        int bonus = 0;

        for (Wave w : mActiveWaves) {
            for (Enemy e : w.getEnemiesToAdd()) {
                bonus += e.getReward();
            }

            for (Enemy e : w.getEnemiesInGame()) {
                bonus += e.getReward();
            }
        }

        return (int)(bonus * mLevel.getSettings().earlyFactor);
    }


    public int getLives() {
        return mLives;
    }

    public void setLives(int lives) {
        mLives = lives;
        onLivesChanged();
    }

    public void giveLives(int count) {
        mLives += count;
        onLivesChanged();
    }

    public void takeLives(int count) {
        mLives -= count;

        if (mLives < 0) {
            onGameOver(false);
        }

        onLivesChanged();
    }


    public boolean isGameOver() {
        return mGameOver;
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

    public void ageTowers() {
        Iterator<Tower> it = mGame.getGameObjects(TypeIds.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            it.next().devalue(mLevel.getSettings().agingFactor);
        }
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
        GameSettings settings = mLevel.getSettings();
        setCredits(settings.credits);
        setLives(settings.lives);

        onBonusChanged();

        for (OnGameStartedListener l : mListeners.get(OnGameStartedListener.class)) {
            l.onGameStarted();
        }
    }

    private void onGameOver(boolean won) {
        mGameOver = true;

        for (OnGameOverListener l : mListeners.get(OnGameOverListener.class)) {
            l.onGameOver(won);
        }
    }

    private void onCreditsChanged() {
        for (OnCreditsChangedListener l : mListeners.get(OnCreditsChangedListener.class)) {
            l.onCreditsChanged(getCredits());
        }
    }

    private void onBonusChanged() {
        for (OnBonusChangedListener l : mListeners.get(OnBonusChangedListener.class)) {
            l.onBonusChanged(getEarlyBonus());
        }
    }

    private void onLivesChanged() {
        for (OnLivesChangedListener l : mListeners.get(OnLivesChangedListener.class)) {
            l.onLivesChanged(getLives());
        }
    }

    @Override
    public void onWaveStarted(Wave wave) {
        if (hasCurrentWave()) {
            getCurrentWave().giveWaveReward();
            giveCredits(getEarlyBonus());
        }

        mActiveWaves.add(wave);
        mNextWaveIndex++;

        for (OnWaveStartedListener l : mListeners.get(OnWaveStartedListener.class)) {
            l.onWaveStarted(wave);
        }

        ageTowers();

        onBonusChanged();
    }

    @Override
    public void onWaveAllEnemiesAdded(Wave wave) {
        if (!isGameOver() && hasNextWave()) {
            onNextWaveReady();
        }
    }

    @Override
    public void onWaveEnemyRemoved(Wave wave, Enemy enemy) {
        onBonusChanged();
    }

    @Override
    public void onWaveDone(Wave wave) {
        mActiveWaves.remove(wave);
        wave.removeListener(this);

        for (OnWaveDoneListener l : mListeners.get(OnWaveDoneListener.class)) {
            l.onWaveDone(wave);
        }

        if (!hasCurrentWave() && !hasNextWave() && !isGameOver()) {
            onGameOver(true);
        }

        onBonusChanged();
    }

    private void onNextWaveReady() {
        for (OnNextWaveReadyListener l : mListeners.get(OnNextWaveReadyListener.class)) {
            l.onNextWaveReady(getNextWave());
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
