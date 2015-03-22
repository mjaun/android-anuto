package ch.bfh.anuto.game.objects;

import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;

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


    protected boolean hasEnemiesInRange() {
        Iterator<GameObject> iterator = mGame.getObjects(Enemy.TYPE_ID);

        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (getDistanceTo(obj) <= mRange) {
                return true;
            }
        }

        return false;
    }

    protected void getEnemiesInRange(List<Enemy> enemies, List<Float> distances) {
        enemies.clear();
        distances.clear();

        Iterator<GameObject> iterator = mGame.getObjects(Enemy.TYPE_ID);

        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            float dist = getDistanceTo(obj);

            if (dist <= mRange) {
                enemies.add((Enemy)obj);
                distances.add(dist);
            }
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
}
