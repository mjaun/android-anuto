package ch.bfh.anuto.game.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

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
}
