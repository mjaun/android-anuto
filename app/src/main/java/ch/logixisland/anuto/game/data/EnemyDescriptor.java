package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.game.objects.Enemy;

public class EnemyDescriptor {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.objects.impl.";

    public Class<? extends Enemy> clazz;

    @Attribute(required=false)
    public int pathIndex;

    @Attribute(required=false)
    public float delay;

    @Attribute(required=false)
    public float offsetX;

    @Attribute(required=false)
    public float offsetY;

    @Attribute(name="clazz")
    private String getClazz() {
        return clazz.getName();
    }

    @Attribute(name="clazz")
    private void setClazz(String className) throws ClassNotFoundException {
        clazz = (Class<? extends Enemy>) Class.forName(CLASS_PREFIX + className);
    }

    public Enemy create() {
        Enemy e;

        try {
            e = clazz.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        }

        return e;
    }
}
