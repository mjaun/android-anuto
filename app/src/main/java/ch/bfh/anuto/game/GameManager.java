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

public class GameManager implements Wave.Listener {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {

    }

    public interface GameListener extends Listener {
        void onGameStart();
        void onGameOver(boolean won);
    }

    public interface WaveListener extends Listener {
        void onWaveStarted(Wave wave);
        void onWaveDone(Wave wave);
    }

    public interface CreditsListener extends Listener {
        void onCreditsChanged(int credits);
    }

    public interface LivesListener extends Listener {
        void onLivesChanged(int lives);
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

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Constructors ------
     */

    public GameManager(Resources res) {
        mGame = new GameEngine(res, this);
        reset();
    }

    /*
    ------ Methods ------
     */

    private void reset() {
        mLevel = null;

        for (Wave w : mActiveWaves) {
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

    public GameEngine getGame() {
        return mGame;
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
        for (Listener l : mListeners) {
            if (l instanceof GameListener) {
                ((GameListener) l).onGameStart();
            }
        }
    }

    private void onGameOver(boolean won) {
        mGameOver = true;

        for (Listener l : mListeners) {
            if (l instanceof GameListener) {
                ((GameListener) l).onGameOver(won);
            }
        }
    }

    private void onCreditsChanged() {
        for (Listener l : mListeners) {
            if (l instanceof CreditsListener) {
                ((CreditsListener)l).onCreditsChanged(mCredits);
            }
        }
    }

    private void onLivesChanged() {
        for (Listener l : mListeners) {
            if (l instanceof LivesListener) {
                ((LivesListener)l).onLivesChanged(mLives);
            }
        }
    }

    @Override
    public void onWaveStarted(Wave wave) {
        if (mActiveWaves.size() > 1) {
            mActiveWaves.get(mActiveWaves.size() - 2).giveReward();
        }


        Iterator<Tower> it = mGame.getGameObjects(TypeIds.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower t = it.next();

            t.setValue((int)(t.getValue() * mLevel.getSettings().agingFactor));
        }

        for (Listener l : mListeners) {
            if (l instanceof WaveListener) {
                ((WaveListener)l).onWaveStarted(wave);
            }
        }
    }

    @Override
    public void onWaveDone(Wave wave) {
        wave.giveReward();
        wave.removeListener(this);
        mActiveWaves.remove(wave);

        for (Listener l : mListeners) {
            if (l instanceof WaveListener) {
                ((WaveListener)l).onWaveDone(wave);
            }
        }

        if (!hasWavesRemaining() && !isGameOver() && mActiveWaves.isEmpty()) {
            onGameOver(true);
        }
    }
}
