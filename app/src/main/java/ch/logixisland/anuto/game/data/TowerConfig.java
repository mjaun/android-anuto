package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.StreamIterator;

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
    public float enhanceBase;

    @Element
    public int maxLevel;

    public Collection<Class<? extends GameObject>> weakAgainst = new ArrayList<>();

    public Collection<Class<? extends GameObject>> strongAgainst = new ArrayList<>();

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

    @Element(name="weakAgainst", required=false)
    private String getWeakAgainst() {
        return StreamIterator.fromIterable(weakAgainst)
                .transform(new Function<Class<? extends GameObject>, String>() {
                    @Override
                    public String apply(Class<? extends GameObject> input) {
                        return input.getName();
                    }
                })
                .toString(";");
    }

    @Element(name="weakAgainst", required=false)
    private void setWeakAgainst(String classNames) throws ClassNotFoundException {
        weakAgainst = StreamIterator.fromArray(classNames.split(";"))
                .transform(new Function<String, Class<? extends GameObject>>() {
                    @Override
                    public Class<? extends GameObject> apply(String input) {
                        try {
                            return (Class<? extends GameObject>) Class.forName(CLASS_PREFIX + input);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    }
                })
                .toList();

        if (weakAgainst.contains(null)) {
            throw new ClassNotFoundException("At least one class was not found: " + classNames);
        }
    }

    @Element(name="strongAgainst", required=false)
    private String getStrongAgainst() {
        return StreamIterator.fromIterable(strongAgainst)
                .transform(new Function<Class<? extends GameObject>, String>() {
                    @Override
                    public String apply(Class<? extends GameObject> input) {
                        return input.getName();
                    }
                })
                .toString(";");
    }

    @Element(name="strongAgainst", required=false)
    private void setStrongAgainst(String classNames) throws ClassNotFoundException {
        strongAgainst = StreamIterator.fromArray(classNames.split(";"))
                .transform(new Function<String, Class<? extends GameObject>>() {
                    @Override
                    public Class<? extends GameObject> apply(String input) {
                        try {
                            return (Class<? extends GameObject>) Class.forName(CLASS_PREFIX + input);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    }
                })
                .toList();

        if (strongAgainst.contains(null)) {
            throw new ClassNotFoundException("At least one class was not found: " + classNames);
        }
    }

    public void commit(Level level) {
        if (upgradeClass != null) {
            upgrade = level.getTowerConfig(upgradeClass);
        }
    }
}
