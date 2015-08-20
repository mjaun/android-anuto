package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;

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

    @ElementList(name="enemies", entry="enemy")
    private ArrayList<EnemyDescriptor> mEnemies = new ArrayList<>();

    @Element(name="waveReward", required=false)
    private int mWaveReward;

    @Element(name="healthMultiplier", required=false)
    private float mHealthMultiplier = 1f;

    @Element(name="rewardMultiplier", required=false)
    private float mRewardMultiplier = 1f;

    private GameEngine mGame;
    private boolean mWaveRewardGiven = false;

    private EnemyDescriptor mNextEnemy;
    private TickTimer mAddTimer = new TickTimer();

    private final ArrayList<EnemyDescriptor> mEnemiesToAdd = new ArrayList<>();
    private final ArrayList<Enemy> mEnemiesInGame = new ArrayList<>();

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Listener Implementations ------
     */

    private final GameObject.Listener mEnemyListener = new GameObject.Listener() {
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
                EnemyDescriptor d = mEnemiesToAdd.remove(0);

                if (d.delay < 0.1f) {
                    addToGame(d);
                } else {
                    mNextEnemy = d;
                    mAddTimer.setInterval(mNextEnemy.delay);
                }
            }

            if (mNextEnemy != null && mAddTimer.tick()) {
                addToGame(mNextEnemy);
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
            e.removeListener(mEnemyListener);
            e.remove();
        }

        mEnemiesInGame.clear();
        mEnemiesToAdd.clear();
        mNextEnemy = null;
    }


    public List<EnemyDescriptor> getEnemies() {
        return mEnemies;
    }


    public int getWaveReward() {
        return mWaveReward;
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


    private void addToGame(EnemyDescriptor d) {
        Enemy e;

        try {
            e = d.clazz.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        }

        e.modifyHealth(mHealthMultiplier);
        e.modifyReward(mRewardMultiplier);

        Level level = GameManager.getInstance().getLevel();
        e.setPath(level.getPaths().get(d.pathIndex));
        e.addListener(mEnemyListener);
        mGame.add(e);
        mEnemiesInGame.add(e);

        e.move(d.offsetX, d.offsetY);
    }
}
