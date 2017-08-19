package ch.logixisland.anuto.business.wave;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.entity.enemy.Enemy;

public class EnemyDefaultHealth {

    private final EntityRegistry mEntityRegistry;
    private final Map<String, Float> mEnemyDefaultHealth;

    public EnemyDefaultHealth(EntityRegistry entityRegistry) {
        mEntityRegistry = entityRegistry;
        mEnemyDefaultHealth = new HashMap<>();
    }

    public float getDefaultHealth(String name) {
        if (!mEnemyDefaultHealth.containsKey(name)) {
            Enemy enemy = (Enemy) mEntityRegistry.createEntity(name);
            mEnemyDefaultHealth.put(name, enemy.getMaxHealth());
        }

        return mEnemyDefaultHealth.get(name);
    }

}
