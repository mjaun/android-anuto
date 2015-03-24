package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import java.util.Iterator;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.objects.AreaEffect;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.util.Iterators;
import ch.bfh.anuto.util.Vector2;

public class LaserEffect extends AreaEffect {

    private static float LASER_WIDTH = 1.0f;
    private static float LASER_LENGTH = 1000f;
    private static float DAMAGE = 100f;

    private final Vector2 mDirection = new Vector2();

    public LaserEffect(Vector2 position, Vector2 direction) {
        mPosition.set(position);
        mDirection.set(direction);
    }

    @Override
    public void init(Resources res) {

    }

    @Override
    public void clean() {

    }

    @Override
    protected void applyEffect() {
        Vector2 lineTo = mDirection.copy().mul(LASER_LENGTH).add(mPosition);
        Iterator<Enemy> enemies = Iterators.cast(mGame.getGameObjects(Enemy.TYPE_ID), Enemy.class);
        Iterator<Enemy> onLine = GameObject.onLine(enemies, mPosition, lineTo, LASER_WIDTH);

        while (onLine.hasNext()) {
            Enemy enemy = onLine.next();
            enemy.damage(DAMAGE);
        }

        mGame.removeGameObject(this);
    }
}
