package ch.bfh.anuto.game.objects.impl;

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
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(this, R.drawable.area_tower);
        mSprite.setLayer(Layers.TOWER);
        mGame.add(mSprite);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mReloaded && getEnemiesInRange().hasNext()) {
            // TODO
        }
    }
}
