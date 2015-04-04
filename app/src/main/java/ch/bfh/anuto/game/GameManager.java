package ch.bfh.anuto.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.game.data.GameSettings;
import ch.bfh.anuto.game.data.Level;
import ch.bfh.anuto.game.data.Wave;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Plateau;
import ch.bfh.anuto.game.objects.Tower;

public class GameManager {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {

    }

    public interface GameListener extends Listener {
        void onGameStart();
        void onGameOver();
    }

    public interface WaveListener extends Listener {
        void onNextWave();
        void onWaveDone();
    }

    public interface CreditsListener extends Listener {
        void onCreditsChanged();
    }

    public interface LivesListener extends Listener {
        void onLivesChanged();
    }

    /*
    ------ Members ------
     */

    private Level mLevel;

    private Wave mWave;
    private int mNextWaveIndex;

    private int mCredits;
    private int mLives;

    private boolean mGameOver = false;

    private Tower mSelectedTower;

    private final GameEngine mGame;
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Constructors ------
     */

    public GameManager(GameEngine game) {
        mGame = game;
    }

    /*
    ------ Methods ------
     */

    public Level getLevel() {
        return mLevel;
    }

    public void loadLevel(Level level) {
        mLevel = level;

        mGame.clear();
        GameSettings settings = mLevel.getSettings();
        mGame.setGameSize(settings.width, settings.height);

        for (Plateau p : level.getPlateaus()) {
            mGame.add(p);
        }

        mWave = null;
        mNextWaveIndex = 0;

        mGameOver = false;
        onGameStart();
        setCredits(settings.credits);
        setLives(settings.lives);
    }


    public Wave getWave() {
        return mWave;
    }

    public int getWaveNum() {
        return mNextWaveIndex;
    }

    public boolean hasWaves() {
        return mNextWaveIndex < mLevel.getWaves().size();
    }

    public void nextWave() {
        if (mWave != null) {
            return;
        }

        if (mNextWaveIndex >= mLevel.getWaves().size()) {
            return;
        }

        mWave = mLevel.getWaves().get(mNextWaveIndex);
        for (Enemy e : mWave.getEnemies()) {
            mGame.add(e);
        }

        mNextWaveIndex++;
        onNextWave();
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
            setGameOver();
        }

        onLivesChanged();
    }


    public void reportEnemyRemoved(Enemy enemy) {
        if (mWave == null) {
            return;
        }

        mWave.getEnemies().remove(enemy);

        if (mWave.getEnemies().isEmpty()) {
            onWaveDone();
            giveCredits(mWave.getReward());
            mWave = null;
        }
    }


    public boolean isGameOver() {
        return mGameOver;
    }

    public void setGameOver() {
        if (!mGameOver) {
            mGameOver = true;
            onGameOver();
        }
    }


    public Tower getSelectedTower() {
        return mSelectedTower;
    }

    public void selectTower(Tower tower) {
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

    private void onGameOver() {
        for (Listener l : mListeners) {
            if (l instanceof GameListener) {
                ((GameListener) l).onGameOver();
            }
        }
    }


    private void onNextWave() {
        for (Listener l : mListeners) {
            if (l instanceof WaveListener) {
                ((WaveListener)l).onNextWave();
            }
        }
    }

    private void onWaveDone() {
        for (Listener l : mListeners) {
            if (l instanceof WaveListener) {
                ((WaveListener)l).onWaveDone();
            }
        }
    }


    private void onCreditsChanged() {
        for (Listener l : mListeners) {
            if (l instanceof CreditsListener) {
                ((CreditsListener)l).onCreditsChanged();
            }
        }
    }


    private void onLivesChanged() {
        for (Listener l : mListeners) {
            if (l instanceof LivesListener) {
                ((LivesListener)l).onLivesChanged();
            }
        }
    }
}
