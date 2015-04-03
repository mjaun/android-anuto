package ch.bfh.anuto.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.game.objects.Tower;

public class GameManager {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onCreditsChanged();
        void onLivesChanged();
        void onTowerSelected();
    }

    /*
    ------ Members ------
     */

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

        onTowerSelected();
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

    private void onTowerSelected() {
        for (Listener l : mListeners) {
            l.onTowerSelected();
        }
    }
}
