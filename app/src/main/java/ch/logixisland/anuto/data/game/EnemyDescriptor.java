package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;

public class EnemyDescriptor extends EntityDescriptor {

    @Element(name = "wave")
    private int mWave;

    @Element(name = "reward")
    private int mReward;

    @Element(name = "health")
    private float mHealth;

    @Element(name = "healthMax")
    private float mHealthMax;

    @Element(name = "pathIndex", required = false)
    private int mPathIndex;

    @Element(name = "wayPointIndex")
    private int mWayPointIndex;

}
