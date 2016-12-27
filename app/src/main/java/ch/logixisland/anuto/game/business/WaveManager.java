package ch.logixisland.anuto.game.business;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.business.score.ScoreBoard;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.data.EnemyDescriptor;
import ch.logixisland.anuto.game.data.WaveDescriptor;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.EntityListener;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
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

    private final GameEngine mGameEngine;
    private final GameManager mGameManager;
    private final ScoreBoard mScoreBoard;

    private final WaveDescriptor mWaveDescriptor;
    private final int mExtend;

    private boolean mAborted;
    private int mEnemiesRemaining;
    private int mEarlyBonus;

    private float mHealthModifier;
    private float mRewardModifier;

    private int mWaveReward;

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Entity.Listener Implementation ------
     */

    private EntityListener mObjectListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity obj) {
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

    public WaveManager(GameEngine gameEngine, GameManager gameManager, ScoreBoard scoreBoard,
                       WaveDescriptor waveDescriptor, int extend) {
        mGameEngine = gameEngine;
        mGameManager = gameManager;
        mScoreBoard = scoreBoard;
        mWaveDescriptor = waveDescriptor;
        mExtend = extend;

        mWaveReward = mWaveDescriptor.getWaveReward();
        mHealthModifier = mWaveDescriptor.getHealthModifier();
        mRewardModifier = mWaveDescriptor.getRewardModifier();
    }

    /*
    ------ Methods ------
     */

    public WaveDescriptor getWaveDescriptor() {
        return mWaveDescriptor;
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
        mGameEngine.post(new Runnable() {
            @Override
            public void run() {
                int delay = 0;
                float offsetX = 0f;
                float offsetY = 0f;

                mAborted = false;
                mEnemiesRemaining = mWaveDescriptor.getEnemies().size() * (mExtend + 1);

                for (int i = 0; i < mExtend + 1; i++) {
                    for (EnemyDescriptor d : mWaveDescriptor.getEnemies()) {
                        if (MathUtils.equals(d.getDelay(), 0f, 0.1f)) {
                            offsetX += d.getOffsetX();
                            offsetY += d.getOffsetY();
                        } else {
                            offsetX = d.getOffsetX();
                            offsetY = d.getOffsetY();
                        }

                        final Enemy e = d.createInstance();
                        e.addListener(mObjectListener);
                        e.modifyHealth(mHealthModifier);
                        e.modifyReward(mRewardModifier);
                        e.setPath(mGameManager.getLevel().getPaths().get(d.getPathIndex()));
                        e.move(offsetX, offsetY);

                        if (i > 0 || mWaveDescriptor.getEnemies().indexOf(d) > 0) {
                            delay += (int)d.getDelay();
                        }

                        mEarlyBonus += e.getReward();

                        mGameEngine.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGameEngine.add(e);
                            }
                        }, delay);
                    }
                }

                onStarted();
            }
        });
    }

    public void abort() {
        mGameEngine.post(new Runnable() {
            @Override
            public void run() {
                mAborted = true;
                onAborted();
            }
        });
    }

    public int getReward() {
        return mWaveReward;
    }

    public void giveReward() {
        mGameEngine.post(new Runnable() {
            @Override
            public void run() {
                mScoreBoard.giveCredits(mWaveReward);
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
