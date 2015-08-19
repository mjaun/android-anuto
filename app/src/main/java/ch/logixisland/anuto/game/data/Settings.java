package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;

import ch.logixisland.anuto.game.objects.Tower;

public class Settings {
    @Element(required=false)
    public int width = 10;

    @Element(required=false)
    public int height = 15;

    @Element(required=false)
    public int credits = 800;

    @Element(required=false)
    public int lives = 5;

    @Element(required=false)
    public float agingFactor = 0.9f;

    @Element(required=false)
    public float earlyFactor = 1.0f;

    @ElementList(name="towers")
    public ArrayList<TowerConfig> towers = new ArrayList<>();


    public TowerConfig getTowerConfig(Tower t) {
        return getTowerConfig(t.getClass());
    }

    public TowerConfig getTowerConfig(Class<? extends Tower> c) {
        for (TowerConfig config : towers) {
            if (config.clazz == c) {
                return config;
            }
        }

        return null;
    }

    public TowerConfig getTowerConfig(int slot) {
        for (TowerConfig config : towers) {
            if (config.slot == slot) {
                return config;
            }
        }

        return null;
    }
}
