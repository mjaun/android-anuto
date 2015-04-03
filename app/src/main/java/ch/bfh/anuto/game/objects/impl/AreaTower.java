package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Tower;
import ch.bfh.anuto.util.math.Vector2;

public class AreaTower extends Tower {

    private final static int RELOAD_TIME = 20;
    private final static float RANGE = 5f;

    private Sprite mSprite;

    public AreaTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public AreaTower(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(this, res, R.drawable.area_tower);
        mGame.addDrawObject(mSprite, Layers.TOWER);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloaded && getEnemiesInRange().hasNext()) {
            // TODO
            //shoot(new ShockWave(mPosition));
        }
    }
}
