package ch.bfh.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Commit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;

public class Wave {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onWaveStarted(Wave wave);
        void onWaveAllEnemiesAdded(Wave wave);
        void onWaveEnemyRemoved(Wave wave, Enemy enemy);
        void onWaveDone(Wave wave);
    }

    /*
    ------ Members ------
     */

    @ElementList(name="enemies")
    private ArrayList<Enemy> mEnemies = new ArrayList<>();

    @Element(name="waveReward", required=false)
    private int mWaveReward = 0;

    @Element(name="healthMultiplier", required=false)
    private float mHealthMultiplier = 1f;

    @Element(name="rewardMultiplier", required=false)
    private float mRewardMultiplier = 1f;

    private GameEngine mGame;
    private boolean mWaveRewardGiven = false;

    private Enemy mNextEnemy;
    private TickTimer mAddTimer = new TickTimer();

    private final ArrayList<Enemy> mEnemiesToAdd = new ArrayList<>();
    private final ArrayList<Enemy> mEnemiesInGame = new ArrayList<>();

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Listener Implementations ------
     */

    private final GameObject.Listener mObjectListener = new GameObject.Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            Enemy e = (Enemy)obj;

            e.removeListener(this);
            mEnemiesInGame.remove(e);

            onWaveEnemyRemoved(e);

            if (mEnemiesInGame.isEmpty() && mEnemiesToAdd.isEmpty() && mNextEnemy == null) {
                onWaveDone();
            }
        }
    };

    private final GameEngine.Listener mGameListener = new GameEngine.Listener() {
        @Override
        public void onTick() {
            if (mEnemiesToAdd.isEmpty() && mNextEnemy == null) {
                onWaveAllEnemiesAdded();
                mGame.removeListener(this);
                return;
            }

            if (mNextEnemy == null) {
                mNextEnemy = mEnemiesToAdd.remove(0);
                mAddTimer.setInterval(mNextEnemy.getAddDelay());
            }

            if (mAddTimer.tick()) {
                mNextEnemy.addListener(mObjectListener);
                mGame.add(mNextEnemy);
                mEnemiesInGame.add(mNextEnemy);
                mNextEnemy = null;
            }
        }
    };

    /*
    ------ Methods ------
     */

    public void start() {
        mWaveRewardGiven = false;
        mEnemiesToAdd.addAll(mEnemies);
        mGame = GameEngine.getInstance();
        mGame.addListener(mGameListener);

        onWaveStarted();
    }

    public void abort() {
        mGame.removeListener(mGameListener);

        for (Enemy e : mEnemiesInGame) {
            e.removeListener(mObjectListener);
            e.remove();
        }

        mEnemiesInGame.clear();
        mEnemiesToAdd.clear();
        mNextEnemy = null;
    }


    public void multiplyHealth(float factor) {
        for (Enemy e : mEnemies) {
            e.setHealthMax(e.getHealthMax() * factor);
            e.setHealth(e.getHealthMax());
        }
    }

    public void multiplyReward(float factor) {
        for (Enemy e : mEnemies) {
            e.setReward(Math.round(e.getReward() * factor));
        }
    }


    public List<Enemy> getEnemies() {
        return mEnemies;
    }

    public List<Enemy> getEnemiesToAdd() {
        return Collections.unmodifiableList(mEnemiesToAdd);
    }

    public List<Enemy> getEnemiesInGame() {
        return Collections.unmodifiableList(mEnemiesInGame);
    }


    public int getWaveReward() {
        return mWaveReward;
    }

    public void setWaveReward(int reward) {
        mWaveReward = reward;
    }

    public void giveWaveReward() {
        if (!mWaveRewardGiven) {
            mWaveRewardGiven = true;
            GameManager.getInstance().giveCredits(mWaveReward);
        }
    }


    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void onWaveStarted() {
        for (Listener l : mListeners) {
            l.onWaveStarted(this);
        }
    }

    private void onWaveAllEnemiesAdded() {
        for (Listener l : mListeners) {
            l.onWaveAllEnemiesAdded(this);
        }
    }

    private void onWaveEnemyRemoved(Enemy enemy) {
        for (Listener l : mListeners) {
            l.onWaveEnemyRemoved(this, enemy);
        }
    }

    private void onWaveDone() {
        giveWaveReward();

        for (Listener l : mListeners) {
            l.onWaveDone(this);
        }
    }


    @Commit
    private void onXmlCommit() {
        multiplyHealth(mHealthMultiplier);
        multiplyReward(mRewardMultiplier);
    }
}
