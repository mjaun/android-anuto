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
        void onWaveChanged();
        void onCreditsChanged();
        void onLivesChanged();
    }

    /*
    ------ Members ------
     */

    private Level mLevel;
    private int mWave;
    private int mCredits;
    private int mLives;
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

        GameSettings settings = mLevel.getSettings();
        mGame.setGameSize(settings.width, settings.height);
        this.setCredits(settings.credits);
        this.setLives(settings.lives);

        for (Plateau p : level.getPlateaus()) {
            mGame.add(p);
        }

        mWave = 0;
    }


    public int getWave() {
        return mWave;
    }

    public void nextWave() {
        if (mWave >= mLevel.getWaves().size()) {
            return;
        }

        Wave wave = mLevel.getWaves().get(mWave);
        for (Enemy e : wave.getEnemies()) {
            mGame.add(e);
        }

        mWave++;
        onWaveChanged();
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
        onLivesChanged();
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

    private void onWaveChanged() {
        for (Listener l : mListeners) {
            l.onWaveChanged();
        }
    }

    private void onCreditsChanged() {
        for (Listener l : mListeners) {
            l.onCreditsChanged();
        }
    }

    private void onLivesChanged() {
        for (Listener l : mListeners) {
            l.onLivesChanged();
        }
    }
}
