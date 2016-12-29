package ch.logixisland.anuto.entity.effect;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.EntityListener;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;

public abstract class AreaEffect extends Effect implements EntityListener {

    private float mRange = 1f;
    private final List<Enemy> mAffectedEnemies = new CopyOnWriteArrayList<>();

    protected AreaEffect(Entity origin, float duration) {
        super(origin, duration);
    }

    @Override
    public void tick() {
        super.tick();

        if (getGameEngine().tick100ms(this)) {
            for (Enemy enemy : mAffectedEnemies) {
                if (getDistanceTo(enemy) > mRange) {
                    mAffectedEnemies.remove(enemy);
                    enemy.removeListener(this);
                    enemyExit(enemy);
                }
            }

            Iterator<Enemy> enemies = getGameEngine().get(Types.ENEMY)
                    .filter(inRange(getPosition(), mRange))
                    .cast(Enemy.class);

            while (enemies.hasNext()) {
                Enemy enemy = enemies.next();

                if (!mAffectedEnemies.contains(enemy)) {
                    mAffectedEnemies.add(enemy);
                    enemy.addListener(this);
                    enemyEnter(enemy);
                }
            }
        }
    }

    @Override
    protected void effectBegin() {

    }

    @Override
    protected void effectEnd() {
        for (Enemy enemy : mAffectedEnemies) {
            enemy.removeListener(this);
            enemyExit(enemy);
        }

        mAffectedEnemies.clear();
    }


    protected abstract void enemyEnter(Enemy e);

    protected abstract void enemyExit(Enemy e);

    @Override
    public void entityRemoved(Entity obj) {
        obj.removeListener(this);
        mAffectedEnemies.remove(obj);
        enemyExit((Enemy)obj);
    }
}
