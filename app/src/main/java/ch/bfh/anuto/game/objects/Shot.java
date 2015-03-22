package ch.bfh.anuto.game.objects;

import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.util.Vector2;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 4;

    public static final float SHOT_INFLUENCE_RANGE = 0.5f;

    /*
    ------ Members ------
     */

    protected float mSpeed;
    protected Vector2 mDirection;

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

        Iterator<GameObject> iterator = mGame.getObjects(Enemy.TYPE_ID);

        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
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

        Iterator<GameObject> iterator = mGame.getObjects(Enemy.TYPE_ID);

        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (getDistanceTo(obj) <= SHOT_INFLUENCE_RANGE) {
                enemies.add((Enemy)obj);
            }
        }
    }
}
