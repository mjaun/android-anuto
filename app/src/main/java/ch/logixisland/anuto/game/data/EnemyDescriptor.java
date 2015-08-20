package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.game.objects.Enemy;

public class EnemyDescriptor {
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
        clazz = (Class<? extends Enemy>) Class.forName(className);
    }
}
