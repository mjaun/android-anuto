package ch.logixisland.anuto.entity.effect;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.Entity;
import ch.logixisland.anuto.engine.logic.EntityListener;
import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;

public abstract class AreaEffect extends Effect {

    private float mRange;

    private final TickTimer mUpdateTimer = TickTimer.createInterval(0.1f);
    private final List<Enemy> mAffectedEnemies = new CopyOnWriteArrayList<>();

    private final EntityListener mEntityListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity obj) {
            obj.removeListener(this);
            mAffectedEnemies.remove(obj);
            enemyExit((Enemy) obj);
        }
    };

    AreaEffect(Entity origin, float duration, float range) {
        super(origin, duration);
        mRange = range;
    }

    @Override
    public void tick() {
        super.tick();

        if (getState() == State.Active && mUpdateTimer.tick()) {
            for (Enemy enemy : mAffectedEnemies) {
                if (getDistanceTo(enemy) > mRange) {
                    mAffectedEnemies.remove(enemy);
                    enemy.removeListener(mEntityListener);
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
                    enemy.addListener(mEntityListener);
                    enemyEnter(enemy);
                }
            }
        }
    }

    @Override
    protected void effectEnd() {
        super.effectEnd();

        for (Enemy enemy : mAffectedEnemies) {
            enemy.removeListener(mEntityListener);
            enemyExit(enemy);
        }

        mAffectedEnemies.clear();
    }


    protected void enemyEnter(Enemy e) {

    }

    protected void enemyExit(Enemy e) {

    }

}
