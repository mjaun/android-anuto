package ch.logixisland.anuto.business.tower;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.entity.tower.Tower;

public class TowerDefaultValue {

    private final EntityRegistry mEntityRegistry;
    private final Map<String, Integer> mTowerDefaultValue;

    public TowerDefaultValue(EntityRegistry entityRegistry) {
        mEntityRegistry = entityRegistry;
        mTowerDefaultValue = new HashMap<>();
    }

    public int getDefaultValue(String name) {
        if (!mTowerDefaultValue.containsKey(name)) {
            Tower tower = (Tower) mEntityRegistry.createEntity(name);
            mTowerDefaultValue.put(name, tower.getValue());
        }

        return mTowerDefaultValue.get(name);
    }

}
