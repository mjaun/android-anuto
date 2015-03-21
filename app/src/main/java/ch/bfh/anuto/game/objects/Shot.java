package ch.bfh.anuto.game.objects;

import android.graphics.PointF;

import java.util.List;

import ch.bfh.anuto.game.GameObject;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 4;

    public static final float SHOT_INFLUENCE_RANGE = 0.5f;

    /*
    ------ Members ------
     */

    protected float mSpeed = 1f;
    protected PointF mDirection = new PointF(1f, 0f);

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        move(mDirection, mSpeed);
    }


    protected Enemy getClosestEnemy() {
        float closestDistance = 0f;
        Enemy closest = null;

        for (GameObject obj : mGame.getObjects(Enemy.TYPE_ID)) {
            float dist = getDistanceTo(obj);

            if (closest == null || dist < closestDistance) {
                closest = (Enemy)obj;
                closestDistance = dist;
            }
        }

        return closest;
    }

    protected void getEnemiesInInfluence(List<Enemy> enemies) {
        enemies.clear();

        for (GameObject obj : mGame.getObjects(Enemy.TYPE_ID)) {
            if (getDistanceTo(obj) <= SHOT_INFLUENCE_RANGE) {
                enemies.add((Enemy)obj);
            }
        }
    }
}
