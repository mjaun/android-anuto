package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;

import ch.logixisland.anuto.game.objects.Tower;

public class TowerConfig {
    public Class<? extends Tower> clazz;
    public Class<? extends Tower> upgrade;

    @Element
    public int value;

    @Element
    public float damage;

    @Element
    public float range;

    @Element
    public float reloadTime;

    @Element(required=false)
    public int slot = -1;

    @Element(name="clazz")
    private String getClazz() {
        return clazz.getName();
    }

    @Element(name="clazz")
    private void setClazz(String className) throws ClassNotFoundException {
        clazz = (Class<? extends Tower>) Class.forName(className);
    }

    @Element(name="upgrade", required=false)
    private String getUpgrade() {
        return upgrade.getName();
    }

    @Element(name="upgrade", required=false)
    private void setUpgrade(String className) throws ClassNotFoundException {
        upgrade = (Class<? extends Tower>) Class.forName(className);
    }
}
