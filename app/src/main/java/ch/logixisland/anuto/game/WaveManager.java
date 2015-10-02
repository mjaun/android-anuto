package ch.logixisland.anuto.game;

import android.os.Handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.data.EnemyDescriptor;
import ch.logixisland.anuto.game.data.Wave;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.util.math.MathUtils;

public class WaveManager {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onStarted(WaveManager m);
        void onAborted(WaveManager m);
        void onFinished(WaveManager m);
        void onEnemyAdded(WaveManager m, Enemy e);
        void onEnemyRemoved(WaveManager m, Enemy e);
    }

    /*
    ------ Members ------
     */

    private GameEngine mGame = GameEngine.getInstance();
    private GameManager mManager = GameManager.getInstance();

    private Wave mWave;
    private int mExtend;
    private Handler mGameHandler;
    private boolean mAborted;
    private int mEnemiesRemaining;
    private int mEarlyBonus;

    private float mHealthModifier;
    private float mRewardModifier;

    private volatile int mWaveReward;

    private List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ GameObject.Listener Implementation ------
     */

    private GameObject.Listener mObjectListener = new GameObject.Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {
        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            mEnemiesRemaining--;
            mEarlyBonus -= ((Enemy)obj).getReward();

            if (mEnemiesRemaining == 0 && !mAborted) {
                onFinished();
            }

            onEnemyRemoved((Enemy)obj);
        }
    };

    /*
    ------ Constructors ------
     */

    public WaveManager(Wave wave, int extend) {
        mWave = wave;
        mExtend = extend;
        mGameHandler = mGame.createHandler();

        mWaveReward = mWave.waveReward;
        mHealthModifier = mWave.healthModifier;
        mRewardModifier = mWave.rewardModifier;
    }

    /*
    ------ Methods ------
     */

    public Wave getWave() {
        return mWave;
    }

    public int getEarlyBonus() {
        return mEarlyBonus;
    }

    public int getExtend() {
        return mExtend;
    }

    public float getHealthModifier() {
        return mHealthModifier;
    }

    public void modifyHealth(float modifier) {
        mHealthModifier *= modifier;
    }

    public float getRewardModifier() {
        return mRewardModifier;
    }

    public void modifyReward(float modifier) {
        mRewardModifier *= modifier;
    }

    public void modifyWaveReward(int modifier) {
        mWaveReward *= modifier;
    }

    public void start() {
        mGameHandler.post(new Runnable() {
            @Override
            public void run() {
                int delay = 0;
                float offsetX = 0f;
                float offsetY = 0f;

                mAborted = false;
                mEnemiesRemaining = mWave.enemies.size() * (mExtend + 1);

                for (int i = 0; i < mExtend + 1; i++) {
                    for (EnemyDescriptor d : mWave.enemies) {
                        if (MathUtils.equals(d.delay, 0f, 0.1f)) {
                            offsetX += d.offsetX;
                            offsetY += d.offsetY;
                        } else {
                            offsetX = d.offsetX;
                            offsetY = d.offsetY;
                        }

                        final Enemy e = d.create();
                        e.addListener(mObjectListener);
                        e.modifyHealth(mHealthModifier);
                        e.modifyReward(mRewardModifier);
                        e.setPath(mManager.getLevel().getPaths().get(d.pathIndex));
                        e.move(offsetX, offsetY);

                        if (i > 0 || mWave.enemies.indexOf(d) > 0) {
                            delay += (int) (d.delay * 1000f);
                        }

                        mEarlyBonus += e.getReward();

                        mGameHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGame.add(e);
                            }
                        }, delay);
                    }
                }

                onStarted();
            }
        });
    }

    public void abort() {
        mGameHandler.post(new Runnable() {
            @Override
            public void run() {
                mGameHandler.removeCallbacksAndMessages(null);
                mAborted = true;
                onAborted();
            }
        });
    }

    public int getReward() {
        return mWaveReward;
    }

    public void giveReward() {
        mGameHandler.post(new Runnable() {
            @Override
            public void run() {
                mManager.giveCredits(mWaveReward, true);
                mWaveReward = 0;
            }
        });
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


    private void onStarted() {
        for (Listener l : mListeners) {
            l.onStarted(this);
        }
    }

    private void onAborted() {
        for (Listener l : mListeners) {
            l.onAborted(this);
        }
    }

    private void onFinished() {
        for (Listener l : mListeners) {
            l.onFinished(this);
        }
    }

    private void onEnemyAdded(Enemy e) {
        for (Listener l : mListeners) {
            l.onEnemyAdded(this, e);
        }
    }

    private void onEnemyRemoved(Enemy e) {
        for (Listener l : mListeners) {
            l.onEnemyRemoved(this, e);
        }
    }
}
