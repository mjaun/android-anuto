package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.game.entity.enemy.Enemy;

public class EnemyConfig {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.entity.enemy.";

    /*
    ------ Fields ------
     */

    private Class<? extends Enemy> enemyClass;

    @Element
    private float health;

    @Element
    private float speed;

    @Element
    private int reward;

    @ElementMap(required=false, entry="property", key="name", attribute=true, inline=true)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<String, Float> properties = new HashMap<>();

    /*
    ------ Methods ------
     */

    @Element(name="clazz")
    private String getEnemyClassName() {
        return enemyClass.getName();
    }

    @Element(name="clazz")
    @SuppressWarnings("unchecked")
    private void setEnemyClassName(String className) throws ClassNotFoundException {
        enemyClass = (Class<? extends Enemy>) Class.forName(CLASS_PREFIX + className);
    }

    Class<? extends Enemy> getEnemyClass() {
        return enemyClass;
    }

    public float getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public int getReward() {
        return reward;
    }

    public Map<String, Float> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
}
