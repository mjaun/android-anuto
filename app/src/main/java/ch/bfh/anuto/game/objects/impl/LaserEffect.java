package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import java.util.Iterator;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.objects.AreaEffect;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.util.iterator.Iterators;
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
    public void init(Resources res) {

    }

    @Override
    public void clean() {

    }

    @Override
    protected void applyEffect() {
        Iterator<Enemy> enemies = Iterators.cast(mGame.getGameObjects(Enemy.TYPE_ID), Enemy.class);
        Iterator<Enemy> onLine = GameObject.onLine(enemies, mPosition, mLaserTo, LASER_WIDTH);

        while (onLine.hasNext()) {
            Enemy enemy = onLine.next();
            enemy.damage(DAMAGE);
        }

        mGame.removeGameObject(this);
    }
}
