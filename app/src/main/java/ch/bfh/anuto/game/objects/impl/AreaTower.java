package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Tower;

public class AreaTower extends Tower {

    private final static int RELOAD_TIME = 20;
    private final static float RANGE = 5f;

    public AreaTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public AreaTower(PointF position) {
        this();
        setPosition(position);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.area_tower);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && hasEnemiesInRange()) {
            // TODO
            //activate(new ShockWave(mPosition));

        }
    }
}
