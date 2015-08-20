package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;

import ch.logixisland.anuto.game.objects.Enemy;

public class EnemyConfig {
    public Class<? extends Enemy> clazz;

    @Element
    public float health;

    @Element
    public float speed;

    @Element
    public int reward;

    @Element(name="clazz")
    private String getClazz() {
        return clazz.getName();
    }

    @Element(name="clazz")
    private void setClazz(String className) throws ClassNotFoundException {
        clazz = (Class<? extends Enemy>) Class.forName(className);
    }
}
