package ch.logixisland.anuto.game.entity.effect;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public abstract class AreaEffect extends Effect {

    /*
    ------ Members ------
     */

    private float mRange = 1f;

    private final List<Enemy> mAffectedEnemies = new CopyOnWriteArrayList<>();

    private final Listener mEnemyListener = new Listener() {
        @Override
        public void onObjectAdded(Entity obj) {

        }

        @Override
        public void onObjectRemoved(Entity obj) {
            obj.removeListener(this);
            mAffectedEnemies.remove(obj);
            enemyExit((Enemy)obj);
        }
    };

    /*
    ------ Constructors ------
     */

    protected AreaEffect(Entity origin, float duration) {
        super(origin, duration);
    }

    /*
    ------ Methods ------
     */

    public StreamIterator<Enemy> getEnemiesInRange() {
        return StreamIterator.fromIterable(mAffectedEnemies);
    }


    @Override
    public void tick() {
        super.tick();

        if (getGameEngine().tick100ms(this) && isInGame()) {
            for (Enemy e : mAffectedEnemies) {
                if (getDistanceTo(e) > mRange) {
                    mAffectedEnemies.remove(e);
                    e.removeListener(mEnemyListener);
                    enemyExit(e);
                }
            }

            StreamIterator<Enemy> enemies = getGameEngine().get(Enemy.TYPE_ID)
                    .filter(inRange(getPosition(), mRange))
                    .cast(Enemy.class);

            while (enemies.hasNext()) {
                Enemy e = enemies.next();

                if (!mAffectedEnemies.contains(e)) {
                    mAffectedEnemies.add(e);
                    e.addListener(mEnemyListener);
                    enemyEnter(e);
                }
            }
        }
    }

    @Override
    protected void effectBegin() {

    }

    @Override
    protected void effectEnd() {
        for (Enemy e : mAffectedEnemies) {
            e.removeListener(mEnemyListener);
            enemyExit(e);
        }

        mAffectedEnemies.clear();
    }


    protected abstract void enemyEnter(Enemy e);

    protected abstract void enemyExit(Enemy e);
}
