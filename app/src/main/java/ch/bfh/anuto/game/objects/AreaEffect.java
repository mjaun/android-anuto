package ch.bfh.anuto.game.objects;

import java.util.List;

import ch.bfh.anuto.game.GameObject;

public abstract class AreaEffect extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 5;

    /*
    ------ Members ------
     */

    protected float mRange;
    protected int mDelay = 0;
    protected boolean mEffectDone = false;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        if (mDelay > 0) {
            mDelay--;
        }

        if (mDelay <= 0 && !mEffectDone) {
            applyEffect();
            mEffectDone = true;
        }
    }

    protected void applyEffect() {

    }


    protected void getEnemiesInRange(List<Enemy> enemies) {
        enemies.clear();

        for (GameObject obj : mGame.getObjects(Enemy.TYPE_ID)) {
            if (getDistanceTo(obj) <= mRange) {
                enemies.add((Enemy)obj);
            }
        }
    }
}
