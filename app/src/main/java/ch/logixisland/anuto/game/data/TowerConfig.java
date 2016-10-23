package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class TowerConfig {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.objects.impl.";

    /*
    ------ Fields ------
     */

    private Class<? extends Tower> towerClass;

    private TowerConfig upgradeTowerConfig;

    private Class<? extends Tower> upgradeTowerClass;

    @Element
    private int value;

    @Element
    private float damage;

    @Element
    private float range;

    @Element
    private float reload;

    @Element
    private int enhanceCost;

    @Element
    private float enhanceDamage;

    @Element
    private float enhanceRange;

    @Element
    private float enhanceReload;

    @Element
    private float enhanceBase;

    @Element
    private int maxLevel;

    private Collection<Class<? extends Enemy>> weakAgainstEnemies = new ArrayList<>();

    private Collection<Class<? extends Enemy>> strongAgainstEnemies = new ArrayList<>();

    @Element(required=false)
    private String damageText;

    @ElementMap(required=false, entry="property", key="name", attribute=true, inline=true)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<String, Float> properties = new HashMap<>();

    @Element(required=false)
    private int slot = -1;

    /*
    ------ Methods ------
     */

    @Element(name="clazz")
    private String getTowerClassName() {
        return towerClass.getName();
    }

    @Element(name="clazz")
    @SuppressWarnings("unchecked")
    private void setTowerClassName(String className) throws ClassNotFoundException {
        towerClass = (Class<? extends Tower>) Class.forName(CLASS_PREFIX + className);
    }

    @Element(name="upgrade", required=false)
    private String getUpgradeTowerClassName() {
        return upgradeTowerClass.getName();
    }

    @Element(name="upgrade", required=false)
    @SuppressWarnings("unchecked")
    private void setUpgradeTowerClassName(String className) throws ClassNotFoundException {
        upgradeTowerClass = (Class<? extends Tower>) Class.forName(CLASS_PREFIX + className);
    }

    @Element(name="weakAgainst", required=false)
    private String getWeakAgainstEnemyNames() {
        return StreamIterator.fromIterable(weakAgainstEnemies)
                .transform(new Function<Class<? extends GameObject>, String>() {
                    @Override
                    public String apply(Class<? extends GameObject> input) {
                        return input.getName();
                    }
                })
                .toString(";");
    }

    @Element(name="weakAgainst", required=false)
    @SuppressWarnings("unchecked")
    private void setWeakAgainstEnemyNames(String classNames) throws ClassNotFoundException {
        weakAgainstEnemies = StreamIterator.fromArray(classNames.split(";"))
                .transform(new Function<String, Class<? extends Enemy>>() {
                    @Override
                    public Class<? extends Enemy> apply(String input) {
                        try {
                            return (Class<? extends Enemy>) Class.forName(CLASS_PREFIX + input);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    }
                })
                .toList();

        if (weakAgainstEnemies.contains(null)) {
            throw new ClassNotFoundException("At least one class was not found: " + classNames);
        }
    }

    @Element(name="strongAgainst", required=false)
    private String getStrongAgainstEnemyNames() {
        return StreamIterator.fromIterable(strongAgainstEnemies)
                .transform(new Function<Class<? extends Enemy>, String>() {
                    @Override
                    public String apply(Class<? extends Enemy> input) {
                        return input.getName();
                    }
                })
                .toString(";");
    }

    @Element(name="strongAgainst", required=false)
    @SuppressWarnings("unchecked")
    private void setStrongAgainstEnemyNames(String classNames) throws ClassNotFoundException {
        strongAgainstEnemies = StreamIterator.fromArray(classNames.split(";"))
                .transform(new Function<String, Class<? extends Enemy>>() {
                    @Override
                    public Class<? extends Enemy> apply(String input) {
                        try {
                            return (Class<? extends Enemy>) Class.forName(CLASS_PREFIX + input);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    }
                })
                .toList();

        if (strongAgainstEnemies.contains(null)) {
            throw new ClassNotFoundException("At least one class was not found: " + classNames);
        }
    }

    Class<? extends Tower> getUpgradeTowerClass() {
        return upgradeTowerClass;
    }

    void setUpgradeTowerConfig(TowerConfig upgradeTowerConfig) {
        this.upgradeTowerConfig = upgradeTowerConfig;
    }

    int getSlot() {
        return slot;
    }

    public Class<? extends Tower> getTowerClass() {
        return towerClass;
    }

    public TowerConfig getUpgradeTowerConfig() {
        return upgradeTowerConfig;
    }

    public Collection<Class<? extends Enemy>> getWeakAgainstEnemies() {
        return Collections.unmodifiableCollection(weakAgainstEnemies);
    }

    public Collection<Class<? extends Enemy>> getStrongAgainstEnemies() {
        return Collections.unmodifiableCollection(strongAgainstEnemies);
    }

    public int getValue() {
        return value;
    }

    public float getDamage() {
        return damage;
    }

    public float getRange() {
        return range;
    }

    public float getReload() {
        return reload;
    }

    public int getEnhanceCost() {
        return enhanceCost;
    }

    public float getEnhanceDamage() {
        return enhanceDamage;
    }

    public float getEnhanceRange() {
        return enhanceRange;
    }

    public float getEnhanceReload() {
        return enhanceReload;
    }

    public float getEnhanceBase() {
        return enhanceBase;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDamageText() {
        return damageText;
    }

    public Map<String, Float> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

}
