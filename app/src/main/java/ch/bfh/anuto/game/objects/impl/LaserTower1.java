package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.List;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.game.objects.Tower;

public class LaserTower1 extends Tower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 0.1f;
    private final static float RANGE = 6.5f;

    private final Sprite mSpriteBase;
    private final Sprite mSpriteTower;

    public LaserTower1() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base5, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.laser_tower1, 4);
        mSpriteTower.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteTower.setMatrix(0.4f, 0.4f, null, null);
        mSpriteTower.setLayer(Layers.TOWER);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSpriteBase);
        mGame.add(mSpriteTower);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteTower);
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloaded && mGame.getTimer100ms().tick()) {
            List<Enemy> enemies = getEnemiesInRange().toList();

            if (enemies.size() > 0) {
                Enemy target = enemies.get(mGame.getRandom(enemies.size()));
                mGame.add(new Laser1(mPosition, target));
                mReloaded = false;
            }
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }
}
