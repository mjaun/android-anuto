package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.game.EnemyDescriptor;
import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.data.game.GameDescriptorRoot;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EnemyPersister implements Persister {

    private final GameEngine mGameEngine;
    private final EntityRegistry mEntityRegistry;
    private final String mEntityName;

    public EnemyPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        mGameEngine = gameEngine;
        mEntityRegistry = entityRegistry;
        mEntityName = entityName;
    }

    @Override
    public void writeDescriptor(GameDescriptorRoot gameDescriptor) {
        StreamIterator<Enemy> iterator = mGameEngine.getEntitiesByType(Types.ENEMY)
                .filter(Entity.nameEquals(mEntityName))
                .cast(Enemy.class);

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            EnemyDescriptor enemyDescriptor = writeEnemyDescriptor(enemy);
            gameDescriptor.addEntityDescriptor(enemyDescriptor);
        }
    }

    @Override
    public void readDescriptor(GameDescriptorRoot gameDescriptor) {
        for (EntityDescriptor entityDescriptor : gameDescriptor.getEntityDescriptors()) {
            if (mEntityName.equals(entityDescriptor.getName())) {
                EnemyDescriptor enemyDescriptor = (EnemyDescriptor) entityDescriptor;
                Enemy enemy = readEnemyDescriptor(enemyDescriptor);
                mGameEngine.add(enemy);
            }
        }
    }

    protected EnemyDescriptor createEnemyDescriptor() {
        return new EnemyDescriptor();
    }

    protected EnemyDescriptor writeEnemyDescriptor(Enemy enemy) {
        EnemyDescriptor enemyDescriptor = createEnemyDescriptor();

        enemyDescriptor.setId(enemy.getEntityId());
        enemyDescriptor.setName(enemy.getEntityName());
        enemyDescriptor.setPosition(enemy.getPosition());
        enemyDescriptor.setHealth(enemy.getHealth());
        enemyDescriptor.setMaxHealth(enemy.getMaxHealth());
        enemyDescriptor.setWayPoints(enemy.getWayPoints());
        enemyDescriptor.setWayPointIndex(enemy.getWayPointIndex());
        enemyDescriptor.setWaveNumber(enemy.getWaveNumber());
        enemyDescriptor.setReward(enemy.getReward());

        return enemyDescriptor;
    }

    protected Enemy readEnemyDescriptor(EnemyDescriptor enemyDescriptor) {
        Enemy enemy = (Enemy) mEntityRegistry.createEntity(enemyDescriptor.getName(), enemyDescriptor.getId());

        enemy.setHealth(enemyDescriptor.getHealth(), enemyDescriptor.getMaxHealth());
        enemy.setReward(enemyDescriptor.getReward());
        enemy.setPosition(enemyDescriptor.getPosition());
        enemy.setWaveNumber(enemyDescriptor.getWaveNumber());
        enemy.setupPath(enemyDescriptor.getWayPoints(), enemyDescriptor.getWayPointIndex());

        return enemy;
    }

    protected GameEngine getGameEngine() {
        return mGameEngine;
    }

    protected EntityRegistry getEntityRegistry() {
        return mEntityRegistry;
    }

}
