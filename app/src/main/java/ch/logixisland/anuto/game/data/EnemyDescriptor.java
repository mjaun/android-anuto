package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.game.entity.enemy.Enemy;

public class EnemyDescriptor {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.entity.enemy.";

    /*
    ------ Fields ------
     */

    private Class<? extends Enemy> enemyClass;

    @Attribute(required=false)
    private int pathIndex;

    @Attribute(required=false)
    private float delay;

    @Attribute(required=false)
    private float offsetX;

    @Attribute(required=false)
    private float offsetY;

    /*
    ------ Methods ------
     */

    @Attribute(name="clazz")
    private String getEnemyClassName() {
        return enemyClass.getName();
    }

    @Attribute(name="clazz")
    @SuppressWarnings("unchecked")
    private void setEnemyClassName(String className) throws ClassNotFoundException {
        enemyClass = (Class<? extends Enemy>) Class.forName(CLASS_PREFIX + className);
    }

    public Enemy createInstance() {
        Enemy enemy;

        try {
            enemy = enemyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return enemy;
    }

    public Class<? extends Enemy> getEnemyClass() {
        return enemyClass;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public float getDelay() {
        return delay;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }
}
