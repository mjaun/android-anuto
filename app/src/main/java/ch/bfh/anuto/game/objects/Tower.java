package ch.bfh.anuto.game.objects;

import android.content.res.Resources;

import java.util.Iterator;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.util.Function;
import ch.bfh.anuto.util.Iterators;

public abstract class Tower extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 3;
    public static final int LAYER = TYPE_ID * 100;

    /*
    ------ Members ------
     */

    protected float mRange;
    protected float mReloadTime;

    protected boolean mReloaded = true;
    private TickTimer mReloadTimer;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init(Resources res) {
        mReloadTimer = TickTimer.createInterval(mReloadTime);
    }

    @Override
    public void tick() {
        if (!mReloaded && mReloadTimer.tick()) {
            mReloaded = true;
        }
    }


    protected void shoot(Shot shot) {
        mGame.addGameObject(shot);
        mReloaded = false;
    }

    protected void shoot(AreaEffect effect) {
        mGame.addGameObject(effect);
        mReloaded = false;
    }

    protected Iterator<Enemy> getEnemiesInRange() {
        Iterator<GameObject> enemies = mGame.getGameObjects(Enemy.TYPE_ID);
        Iterator<GameObject> inRange = GameObject.inRange(enemies, mPosition, mRange);

        return Iterators.cast(inRange, Enemy.class);
    }
}
