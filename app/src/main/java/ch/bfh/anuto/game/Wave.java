package ch.bfh.anuto.game;

import android.graphics.PointF;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wave {
    /*
    ------ Members ------
     */

    @ElementList(name="enemies")
    private ArrayList<Enemy> mEnemies;

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
}
