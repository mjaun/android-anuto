package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.HashMap;

import ch.logixisland.anuto.game.objects.Tower;

public class TowerConfig {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.objects.impl.";

    public Class<? extends Tower> clazz;

    public TowerConfig upgrade;
    private Class<? extends Tower> upgradeClass;

    @Element
    public int value;

    @Element
    public float damage;

    @Element
    public float range;

    @Element
    public float reload;

    @Element
    public int enhanceCost;

    @Element
    public float enhanceDamage;

    @Element
    public float enhanceRange;

    @Element
    public float enhanceReload;

    @Element
    public int maxLevel;

    @Element(required=false)
    public String damageText;

    @ElementMap(required=false, entry="property", key="name", attribute=true, inline=true)
    public HashMap<String, Float> properties = new HashMap<>();

    @Element(required=false)
    public int slot = -1;

    @Element(name="clazz")
    private String getClazz() {
        return clazz.getName();
    }

    @Element(name="clazz")
    private void setClazz(String className) throws ClassNotFoundException {
        clazz = (Class<? extends Tower>) Class.forName(CLASS_PREFIX + className);
    }

    @Element(name="upgrade", required=false)
    private String getUpgrade() {
        return upgradeClass.getName();
    }

    @Element(name="upgrade", required=false)
    private void setUpgrade(String className) throws ClassNotFoundException {
        upgradeClass = (Class<? extends Tower>) Class.forName(CLASS_PREFIX + className);
    }

    public void commit(Level level) {
        if (upgradeClass != null) {
            upgrade = level.getTowerConfig(upgradeClass);
        }
    }
}
