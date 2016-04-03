package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;

public class Wave {

    @ElementList(entry="enemy")
    public ArrayList<EnemyDescriptor> enemies = new ArrayList<>();

    @Element(required=false)
    public int waveReward = 0;

    @Element(required=false)
    public float healthModifier = 1f;

    @Element(required=false)
    public float rewardModifier = 1f;

    @Element(required=false)
    public int extend = 0;

    @Element(required=false)
    public int maxExtend = 0;

    @Element(required=false)
    public float nextWaveDelay = 10;
}
