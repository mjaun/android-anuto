package ch.bfh.anuto.game.objects.impl;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.objects.AreaEffect;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.Vector2;

public class LaserEffect extends AreaEffect {

    private final static float LASER_WIDTH = 1.0f;
    private final static float DAMAGE = 100f;

    private final Vector2 mLaserTo = new Vector2();

    public LaserEffect(Vector2 position, Vector2 laserTo) {
        mPosition.set(position);
        mLaserTo.set(laserTo);
    }

    @Override
    protected void applyEffect() {
        StreamIterator<Enemy> enemies = mGame.getGameObjects(Enemy.TYPE_ID)
                .filter(GameObject.onLine(mPosition, mLaserTo, LASER_WIDTH))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(DAMAGE);
        }

        this.remove();
    }
}
