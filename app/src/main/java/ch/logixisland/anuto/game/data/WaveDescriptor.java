package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaveDescriptor {

    /*
    ------ Fields ------
     */

    @ElementList(entry="enemy")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<EnemyDescriptor> enemies = new ArrayList<>();

    @Element(required=false)
    private int waveReward = 0;

    @Element(required=false)
    private float healthModifier = 1f;

    @Element(required=false)
    private float rewardModifier = 1f;

    @Element(required=false)
    private int extend = 0;

    @Element(required=false)
    private int maxExtend = 0;

    @Element(required=false)
    private float nextWaveDelay = 10;

    /*
    ------ Methods ------
     */

    public List<EnemyDescriptor> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public int getWaveReward() {
        return waveReward;
    }

    public float getHealthModifier() {
        return healthModifier;
    }

    public float getRewardModifier() {
        return rewardModifier;
    }

    public int getExtend() {
        return extend;
    }

    public int getMaxExtend() {
        return maxExtend;
    }

    public float getNextWaveDelay() {
        return nextWaveDelay;
    }
}
