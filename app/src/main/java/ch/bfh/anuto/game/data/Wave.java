package ch.bfh.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Commit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.Enemy;

public class Wave implements GameEngine.Listener, GameObject.Listener {

    /*
    ------ Listener Interface
     */

    public interface Listener {
        void onWaveStarted(Wave wave);
        void onWaveDone(Wave wave);
    }

    /*
    ------ Members ------
     */

    @ElementList(name="enemies")
    private ArrayList<Enemy> mEnemies;

    @Element(name="reward", required=false)
    private int mReward = 0;

    @Element(name="healthMultiplier", required=false)
    private float mHealthMultiplier = 1f;

    @Element(name="rewardMultiplier", required=false)
    private float mRewardMultiplier = 1f;

    private GameEngine mGame;

    private Enemy mNextEnemy;
    private TickTimer mAddTimer = new TickTimer();

    private ArrayList<Enemy> mEnemiesToAdd = new ArrayList<>();
    private ArrayList<Enemy> mEnemiesInGame = new ArrayList<>();

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();


    /*
    ------ Constructors ------
     */

    public Wave() {
        mEnemies = new ArrayList<>();
    }

    public Wave(Enemy... enemies) {
        mEnemies = new ArrayList<>(Arrays.asList(enemies));
    }

    /*
    ------ Public Methods ------
     */

    public GameEngine getGame() {
        return mGame;
    }

    public void setGame(GameEngine game) {
        mGame = game;
    }

    public void start() {
        mEnemiesToAdd.addAll(mEnemies);
        mGame.addListener(this);

        onWaveStarted();
    }

    public void stop() {
        mGame.removeListener(this);

        mEnemiesToAdd.clear();
        mNextEnemy = null;
    }


    public List<Enemy> getEnemies() {
        return mEnemies;
    }


    public int getReward() {
        return mReward;
    }

    public void setReward(int reward) {
        mReward = reward;
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

    @Commit
    private void onXmlCommit() {
        multiplyHealth(mHealthMultiplier);
        multiplyReward(mRewardMultiplier);
    }


    @Override
    public void onTick() {
        if (mEnemiesToAdd.isEmpty() && mNextEnemy == null) {
            mGame.removeListener(this);
            return;
        }

        if (mNextEnemy == null) {
            mNextEnemy = mEnemiesToAdd.remove(0);
            mAddTimer.setInterval(mNextEnemy.getAddDelay());
        }

        if (mAddTimer.tick()) {
            mGame.add(mNextEnemy);
            mEnemiesInGame.add(mNextEnemy);
            mNextEnemy = null;
        }
    }

    @Override
    public void onObjectAdded(GameObject obj) {

    }

    @Override
    public void onObjectRemoved(GameObject obj) {
        Enemy e = (Enemy)obj;

        e.removeListener(this);
        mEnemiesInGame.remove(e);

        if (mEnemiesToAdd.isEmpty() && mEnemiesInGame.isEmpty()) {
            onWaveDone();
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

    private void onWaveDone() {
        mGame.getManager().giveCredits(mReward);

        for (Listener l : mListeners) {
            l.onWaveDone(this);
        }
    }
}
