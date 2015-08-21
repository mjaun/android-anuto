package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;

public class Wave {

    @ElementList(entry="enemy")
    public ArrayList<EnemyDescriptor> enemies = new ArrayList<>();

    @Element(required=false)
    public int waveReward;

    @Element(required=false)
    public float healthMultiplier = 1f;

    @Element(required=false)
    public float rewardMultiplier = 1f;
}
