package ch.bfh.anuto.game.objects;

import java.util.Iterator;

import ch.bfh.anuto.game.GameObject;
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
        if (mReloadCounter > 0) {
            mReloadCounter -= 1f;
        }
    }


    protected boolean isReloaded() {
        return mReloadCounter <= 0;
    }

    protected void shoot(Shot shot) {
        mGame.addGameObject(shot);
        mReloadCounter += mReloadTime;
    }

    protected void activate(AreaEffect effect) {
        mGame.addGameObject(effect);
        mReloadCounter = mReloadTime;
    }

    protected Iterator<Enemy> getEnemiesInRange() {
        Iterator<GameObject> enemies = mGame.getGameObjects(Enemy.TYPE_ID);
        Iterator<GameObject> inRange = GameObject.inRange(enemies, mPosition, mRange);

        return Iterators.transform(inRange, new Function<GameObject, Enemy>() {
            @Override
            public Enemy apply(GameObject input) {
                return (Enemy)input;
            }
        });
    }
}
