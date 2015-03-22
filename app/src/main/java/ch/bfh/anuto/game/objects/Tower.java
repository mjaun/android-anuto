package ch.bfh.anuto.game.objects;

import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.util.Function;
import ch.bfh.anuto.util.Iterators;

public abstract class Tower extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 3;

    /*
    ------ Members ------
     */

    protected float mRange;
    protected float mReloadTime;

    protected float mReloadCounter = 0;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloadCounter > 0) {
            mReloadCounter -= 1f;
        }
    }


    protected boolean isReloaded() {
        return mReloadCounter <= 0;
    }

    protected void shoot(Shot shot) {
        mGame.addObject(shot);
        mReloadCounter += mReloadTime;
    }

    protected void activate(AreaEffect effect) {
        mGame.addObject(effect);
        mReloadCounter = mReloadTime;
    }

    protected Iterator<Enemy> getEnemiesInRange() {
        Iterator<GameObject> enemies = mGame.getObjects(Enemy.TYPE_ID);
        Iterator<GameObject> inRange = GameObject.inRange(enemies, mPosition, mRange);

        return Iterators.transform(inRange, new Function<GameObject, Enemy>() {
            @Override
            public Enemy apply(GameObject input) {
                return (Enemy)input;
            }
        });
    }
}
