package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Tower;

@Root
public class Level {

    /*
    ------ Fields ------
     */

    @Element(name="settings")
    private Settings settings = new Settings();

    @ElementList(name="towers", entry="tower")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<TowerConfig> towers = new ArrayList<>();

    @ElementList(name="enemies", entry="enemy")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<EnemyConfig> enemies = new ArrayList<>();

    @ElementList(name="plateaus")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<PlateauDescriptor> plateaus = new ArrayList<>();

    @ElementList(name="paths")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Path> paths = new ArrayList<>();

    @ElementList(name="waves")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Wave> waves = new ArrayList<>();

    /*
    ------ Methods ------
     */

    public static Level deserialize(InputStream inStream) throws Exception {
        Strategy strategy = new CycleStrategy("id", "ref");
        Serializer serializer = new Persister(strategy);
        return serializer.read(Level.class, inStream);
    }

    public Settings getSettings() {
        return settings;
    }

    public List<PlateauDescriptor> getPlateaus() {
        return Collections.unmodifiableList(plateaus);
    }

    public List<Path> getPaths() {
        return Collections.unmodifiableList(paths);
    }

    public List<Wave> getWaves() {
        return Collections.unmodifiableList(waves);
    }

    public TowerConfig getTowerConfig(Tower t) {
        return getTowerConfig(t.getClass());
    }

    private TowerConfig getTowerConfig(Class<? extends Tower> c) {
        for (TowerConfig config : towers) {
            if (config.getTowerClass() == c) {
                return config;
            }
        }

        throw new RuntimeException("No config found for this tower class!");
    }

    public TowerConfig getTowerConfig(int slot) {
        for (TowerConfig config : towers) {
            if (config.getSlot() == slot) {
                return config;
            }
        }

        return null;
    }

    public EnemyConfig getEnemyConfig(Enemy e) {
        return getEnemyConfig(e.getClass());
    }

    public EnemyConfig getEnemyConfig(Class<? extends Enemy> c) {
        for (EnemyConfig config : enemies) {
            if (config.getEnemyClass() == c) {
                return config;
            }
        }

        throw new RuntimeException("No config found for this enemy class!");
    }

    @Commit
    void commit() {
        for (TowerConfig config : towers) {
            Class<? extends Tower> upgradeClass = config.getUpgradeTowerClass();

            if (upgradeClass != null) {
                config.setUpgradeTowerConfig(getTowerConfig(upgradeClass));
            }
        }
    }
}
