package ch.bfh.anuto.game.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Commit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.bfh.anuto.game.objects.Enemy;

public class Wave {
    /*
    ------ Members ------
     */

    @ElementList(name="enemies")
    private ArrayList<Enemy> mEnemies;

    @Element(name="reward")
    private int mReward;

    @Element(name="healthMultiplier", required=false)
    private float mHealthMultiplier = 1f;

    @Element(name="rewardMultiplier", required=false)
    private float mRewardMultiplier = 1f;

    /*
    ------ Constructors ------
     */

    public Wave() {
        mEnemies = new ArrayList<>();
    }

    public Wave(Enemy... enemies) {
        mEnemies = new ArrayList<>(Arrays.asList(enemies));
    }

    /*
    ------ Public Methods ------
     */

    public List<Enemy> getEnemies() {
        return mEnemies;
    }

    public int getReward() {
        return mReward;
    }

    public void setReward(int reward) {
        mReward = reward;
    }


    public void multiplyHealth(float factor) {
        for (Enemy e : mEnemies) {
            e.setHealth(e.getHealth() * factor);
        }
    }

    public void multiplyReward(float factor) {
        for (Enemy e : mEnemies) {
            e.setReward(Math.round(e.getReward() * factor));
        }
    }

    @Commit
    private void onCommit() {
        multiplyHealth(mHealthMultiplier);
        multiplyReward(mRewardMultiplier);
    }
}
