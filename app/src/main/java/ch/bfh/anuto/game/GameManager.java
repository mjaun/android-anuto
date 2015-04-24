package ch.bfh.anuto.game;

import android.content.res.Resources;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.game.data.GameSettings;
import ch.bfh.anuto.game.data.Level;
import ch.bfh.anuto.game.data.Wave;
import ch.bfh.anuto.game.objects.Plateau;
import ch.bfh.anuto.game.objects.Tower;
import ch.bfh.anuto.util.container.ListenerList;

public class GameManager implements Wave.Listener {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {

    }

    public interface GameStartListener extends Listener {
        void onGameStart();
    }

    public interface GameOverListener extends Listener {
        void onGameOver(boolean won);
    }

    public interface WaveStartedListener extends Listener {
        void onWaveStarted(Wave wave);
    }

    public interface WaveDoneListener extends Listener {
        void onWaveDone(Wave wave);
    }

    public interface CreditsChangedListener extends Listener {
        void onCreditsChanged(int credits);
    }

    public interface LivesChangedListener extends Listener {
        void onLivesChanged(int lives);
    }

    public interface ShowTowerInfoListener extends Listener {
        void onShowTowerInfo(Tower tower);
    }

    public interface HideTowerInfoListener extends Listener {
        void onHideTowerInfo();
    }

    /*
    ------ Members ------
     */

    private Level mLevel;

    private int mNextWaveIndex;

    private int mCredits;
    private int mLives;

    private boolean mGameOver = true;
    private int mEarlyBonus;

    private Tower mSelectedTower;

    private final GameEngine mGame;
    private final List<Wave> mActiveWaves = new CopyOnWriteArrayList<>();

    private final ListenerList<Listener> mListeners = new ListenerList<>();

    /*
    ------ Constructors ------
     */

    public GameManager(Resources res) {
        mGame = new GameEngine(res, this);
    }

    /*
    ------ Methods ------
     */

    public GameEngine getGame() {
        return mGame;
    }


    private void reset() {
        mLevel = null;

        for (Wave w : mActiveWaves) {
            w.abort();
        }

        mActiveWaves.clear();

        mGame.reset();

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
        if (mLevel != null) {
            reset();
        }

        mLevel = level;

        for (Plateau p : level.getPlateaus()) {
            mGame.add(p);
        }

        GameSettings settings = mLevel.getSettings();
        mGame.setGameSize(settings.width, settings.height);

        setCredits(settings.credits);
        setLives(settings.lives);

        onGameStart();
    }


    public int getWaveNumber() {
        return mNextWaveIndex;
    }

    public boolean hasWavesRemaining() {
        return mNextWaveIndex < mLevel.getWaves().size();
    }

    public Wave getNextWave() {
        return mLevel.getWaves().get(mNextWaveIndex);
    }

    public void callNextWave() {
        Wave wave = mLevel.getWaves().get(mNextWaveIndex);
        mNextWaveIndex++;

        wave.addListener(this);
        wave.setGame(mGame);
        wave.start();

        mActiveWaves.add(wave);
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

    /*
    ------ Listener Stuff ------
     */

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }


    private void onGameStart() {
        Iterator<GameStartListener> it = mListeners.get(GameStartListener.class);
        while (it.hasNext()) {
            it.next().onGameStart();
        }
    }

    private void onGameOver(boolean won) {
        mGameOver = true;

        Iterator<GameOverListener> it = mListeners.get(GameOverListener.class);
        while (it.hasNext()) {
            it.next().onGameOver(won);
        }
    }

    private void onCreditsChanged() {
        Iterator<CreditsChangedListener> it = mListeners.get(CreditsChangedListener.class);
        while (it.hasNext()) {
            it.next().onCreditsChanged(mCredits);
        }
    }

    private void onLivesChanged() {
        Iterator<LivesChangedListener> it = mListeners.get(LivesChangedListener.class);
        while (it.hasNext()) {
            it.next().onLivesChanged(mLives);
        }
    }

    @Override
    public void onWaveStarted(Wave wave) {
        if (mActiveWaves.size() > 1) {
            mActiveWaves.get(mActiveWaves.size() - 2).giveWaveReward();

            if (mEarlyBonus > 0) {
                giveCredits(mEarlyBonus);
            }
        }

        if (hasWavesRemaining()) {
            float factor = mLevel.getSettings().earlyBonusFactor;
            mEarlyBonus = (int)(getNextWave().getWaveReward() * factor);
        }

        Iterator<Tower> it = mGame.getGameObjects(TypeIds.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower t = it.next();

            t.setValue((int)(t.getValue() * mLevel.getSettings().agingFactor));
        }

        Iterator<WaveStartedListener> it2 = mListeners.get(WaveStartedListener.class);
        while (it2.hasNext()) {
            it2.next().onWaveStarted(wave);
        }
    }

    @Override
    public void onWaveDone(Wave wave) {
        wave.giveWaveReward();
        wave.removeListener(this);
        mActiveWaves.remove(wave);

        Iterator<WaveDoneListener> it = mListeners.get(WaveDoneListener.class);
        while (it.hasNext()) {
            it.next().onWaveDone(wave);
        }

        if (!hasWavesRemaining() && !isGameOver() && mActiveWaves.isEmpty()) {
            onGameOver(true);
        }
    }

    private void onShowTowerInfo(Tower tower) {
        Iterator<ShowTowerInfoListener> it = mListeners.get(ShowTowerInfoListener.class);
        while (it.hasNext()) {
            it.next().onShowTowerInfo(tower);
        }
    }

    private void onHideTowerInfo() {
        Iterator<HideTowerInfoListener> it = mListeners.get(HideTowerInfoListener.class);
        while (it.hasNext()) {
            it.next().onHideTowerInfo();
        }
    }
}
