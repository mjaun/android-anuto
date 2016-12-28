package ch.logixisland.anuto.game.business;

import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.business.control.TowerSelector;
import ch.logixisland.anuto.game.business.level.LevelLoader;
import ch.logixisland.anuto.game.business.level.WaveAttender;
import ch.logixisland.anuto.game.business.score.LivesListener;
import ch.logixisland.anuto.game.business.score.ScoreBoard;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.data.EnemyDescriptor;
import ch.logixisland.anuto.game.data.LevelDescriptor;
import ch.logixisland.anuto.game.data.Settings;
import ch.logixisland.anuto.game.data.WaveDescriptor;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.render.Viewport;
import ch.logixisland.anuto.util.container.ListenerList;
import ch.logixisland.anuto.util.math.MathUtils;

public class GameManager implements LivesListener {

    /*
    ------ Constants ------
     */

    private final static String TAG = GameManager.class.getSimpleName();

    /*
    ------ Listener Interface ------
     */

    public interface Listener {

    }

    public interface OnGameStartedListener extends Listener {
        void onGameStarted();
    }

    public interface OnGameOverListener extends Listener {
        void onGameOver();
    }

    public interface OnWaveStartedListener extends Listener {
        void onWaveStarted(WaveDescriptor waveDescriptor);
    }

    public interface OnNextWaveReadyListener extends Listener {
        void onNextWaveReady(WaveDescriptor waveDescriptor);
    }

    public interface OnWaveDoneListener extends Listener {
        void onWaveDone(WaveDescriptor waveDescriptor);
    }

    public interface OnTowersAgedListener extends Listener {
        void onTowersAged();
    }

    /*
    ------ Members ------
     */

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final TowerSelector mTowerSelector;
    private final Viewport mViewport;
    private final LevelLoader mLevelLoader;

    private int mNextWaveIndex;

    private volatile boolean mGameOver;
    private volatile boolean mNextWaveReady;

    private List<WaveAttender> mActiveWaves = new CopyOnWriteArrayList<>();

    private ListenerList<Listener> mListeners = new ListenerList<>();

    /*
    ------ WaveAttender.Listener Implementation ------
     */

    private WaveAttender.Listener mWaveListener = new WaveAttender.Listener() {
        @Override
        public void onStarted(final WaveAttender m) {
            mActiveWaves.add(m);
            calcEarlyBonus();

            mGameEngine.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getCurrentWaveManager() == m && !mNextWaveReady) {
                        onNextWaveReady();
                        mNextWaveReady = true;
                    }
                }
            }, m.getWaveDescriptor().getNextWaveDelay());

            onWaveStarted(m.getWaveDescriptor());
        }

        @Override
        public void onAborted(WaveAttender m) {
        }

        @Override
        public void onFinished(WaveAttender m) {
            if (getCurrentWaveManager() == m && !mNextWaveReady) {
                onNextWaveReady();
                mNextWaveReady = true;
            }

            mActiveWaves.remove(m);
            m.giveReward();

            ageTowers();
            onWaveDone(m.getWaveDescriptor());
        }

        @Override
        public void onEnemyAdded(WaveAttender m, Enemy e) {
            mActiveWaves.remove(m);
        }

        @Override
        public void onEnemyRemoved(WaveAttender m, Enemy e) {
            calcEarlyBonus();
        }
    };

    /*
    ------ Constructors ------
     */

    public GameManager(GameEngine gameEngine, Viewport viewport, ScoreBoard scoreBoard,
                       TowerSelector towerSelector, LevelLoader levelLoader) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mViewport = viewport;
        mTowerSelector = towerSelector;
        mLevelLoader = levelLoader;

        mGameOver = true;
        mScoreBoard.addLivesListener(this);
    }

    /*
    ------ Methods ------
     */

    private void reset() {

    }

    public void setLevel(LevelDescriptor level) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final LevelDescriptor finalLevel = level;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    setLevel(finalLevel);
                }
            });
            return;
        }

        mLevelLoader.setLevel(level);
        restart();
    }

    public void restart() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    restart();
                }
            });
            return;
        }

        mLevelLoader.reset();

        for (WaveAttender m : mActiveWaves) {
            m.abort();
        }

        mActiveWaves.clear();
        mNextWaveIndex = 0;
        mGameOver = false;

        onGameStarted();
    }

    public int getWaveNumber() {
        return mNextWaveIndex;
    }

    public boolean hasCurrentWave() {
        return !mActiveWaves.isEmpty();
    }

    public WaveDescriptor getCurrentWave() {
        if (!hasCurrentWave()) {
            return null;
        }

        return getCurrentWaveManager().getWaveDescriptor();
    }

    private WaveAttender getCurrentWaveManager() {
        if (!hasCurrentWave()) {
            return null;
        }

        return mActiveWaves.get(mActiveWaves.size() - 1);
    }

    public WaveDescriptor getNextWave() {
        List<WaveDescriptor> waveDescriptors = mLevelLoader.getLevel().getWaveDescriptors();
        return waveDescriptors.get(mNextWaveIndex % waveDescriptors.size());
    }

    public int getWaveIterationCount() {
        List<WaveDescriptor> waveDescriptors = mLevelLoader.getLevel().getWaveDescriptors();
        return mNextWaveIndex / waveDescriptors.size();
    }

    public void startNextWave() {
        if (hasCurrentWave()) {
            getCurrentWaveManager().giveReward();
            mScoreBoard.giveCredits(mScoreBoard.getEarlyBonus());
        }

        WaveDescriptor nextWaveDescriptor = getNextWave();
        int extend = nextWaveDescriptor.getExtend() * getWaveIterationCount();

        if (nextWaveDescriptor.getMaxExtend() > 0 && extend > nextWaveDescriptor.getMaxExtend()) {
            extend = nextWaveDescriptor.getMaxExtend();
        }

        WaveAttender m = new WaveAttender(mGameEngine, this, mScoreBoard, mLevelLoader, nextWaveDescriptor, extend);
        m.addListener(mWaveListener);

        calcWaveModifiers(m);
        m.start();

        mNextWaveIndex++;
        mNextWaveReady = false;
    }

    public int getWaveBonus() {
        if (!hasCurrentWave()) {
            return 0;
        }

        return getCurrentWaveManager().getReward();
    }

    @Override
    public void livesChanged(int lives) {
        if (!mGameOver && mScoreBoard.getLives() < 0) {
            mGameOver = true;
            onGameOver();
        }
    }


    public boolean isGameOver() {
        return mGameOver;
    }


    private void ageTowers() {
        Iterator<Tower> it = mGameEngine.get(Types.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower t = it.next();
            t.devalue(getSettings().getAgeModifier());
        }

        onTowersAged();
    }

    private void calcEarlyBonus() {
        float earlyBonus = 0;

        for (WaveAttender m : mActiveWaves) {
            earlyBonus += m.getEarlyBonus();
        }

        float modifier = getSettings().getEarlyModifier();
        float root = getSettings().getEarlyRoot();

        mScoreBoard.setEarlyBonus(Math.round(modifier * (float)Math.pow(earlyBonus, 1f / root)));
        mScoreBoard.setWaveBonus(getWaveBonus());
    }

    private void calcWaveModifiers(WaveAttender waveMan) {
        Log.d(TAG, String.format("calculating wave modifiers for wave %d...", getWaveNumber() + 1));
        Log.d(TAG, String.format("creditsEarned=%d", mScoreBoard.getCreditsEarned()));

        float waveHealth = 0f;

        for (EnemyDescriptor d : waveMan.getWaveDescriptor().getEnemies()) {
            waveHealth += mLevelLoader.getLevel().getEnemyConfig(d.getEnemyClass()).getHealth();
        }

        waveHealth *= waveMan.getExtend() + 1;

        Log.d(TAG, String.format("waveHealth=%f", waveHealth));

        float damagePossible = getSettings().getDifficultyOffset()
                + getSettings().getDifficultyLinear() * mScoreBoard.getCreditsEarned()
                + getSettings().getDifficultyQuadratic() * MathUtils.square(mScoreBoard.getCreditsEarned());
        float healthModifier = damagePossible / waveHealth;

        waveMan.modifyHealth(healthModifier);

        float rewardModifier = getSettings().getRewardModifier()
                * (float)Math.pow(waveMan.getHealthModifier(), 1f / getSettings().getRewardRoot());

        if (rewardModifier < 1f) {
            rewardModifier = 1f;
        }

        waveMan.modifyReward(rewardModifier);
        waveMan.modifyWaveReward((getWaveNumber() / mLevelLoader.getLevel().getWaveDescriptors().size()) + 1);

        Log.d(TAG, String.format("waveNumber=%d", getWaveNumber()));
        Log.d(TAG, String.format("damagePossible=%f\n", damagePossible));
        Log.d(TAG, String.format("healthModifier=%f", waveMan.getHealthModifier()));
        Log.d(TAG, String.format("rewardModifier=%f", waveMan.getRewardModifier()));
    }

    private Settings getSettings() {
        return mLevelLoader.getLevel().getSettings();
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
        Log.i(TAG, "Game started.");

        for (OnGameStartedListener l : mListeners.get(OnGameStartedListener.class)) {
            l.onGameStarted();
        }
    }

    private void onGameOver() {
        Log.i(TAG, "Game over.");

        for (OnGameOverListener l : mListeners.get(OnGameOverListener.class)) {
            l.onGameOver();
        }
    }

    public void onWaveStarted(WaveDescriptor waveDescriptor) {
        Log.i(TAG, "Wave started.");

        for (OnWaveStartedListener l : mListeners.get(OnWaveStartedListener.class)) {
            l.onWaveStarted(waveDescriptor);
        }
    }

    private void onNextWaveReady() {
        Log.i(TAG, "Next wave ready.");

        for (OnNextWaveReadyListener l : mListeners.get(OnNextWaveReadyListener.class)) {
            l.onNextWaveReady(getNextWave());
        }
    }

    public void onWaveDone(WaveDescriptor waveDescriptor) {
        Log.i(TAG, "Wave done.");

        for (OnWaveDoneListener l : mListeners.get(OnWaveDoneListener.class)) {
            l.onWaveDone(waveDescriptor);
        }
    }

    private void onTowersAged() {
        for (OnTowersAgedListener l : mListeners.get(OnTowersAgedListener.class)) {
            l.onTowersAged();
        }
    }
}
