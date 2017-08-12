package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;

import ch.logixisland.anuto.util.math.Vector2;

public class EnemyDescriptor {

    @Element(name = "name")
    private String mName;

    @Element(name = "wave")
    private int mWave;

    @Element(name = "pathIndex", required = false)
    private int mPathIndex;

    @Element(name = "wayPointIndex")
    private int mWayPointIndex;

    @Element(name = "position")
    private Vector2 mPosition;

    @Element(name = "reward")
    private int mReward;

    @Element(name = "health")
    private float mHealth;

    @Element(name = "healthMax")
    private float mHealthMax;

}
