package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.math.Vector2;

public class MineLayer extends Tower {

    private final static float ANIMATION_DURATION = 1f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
    }

    private float mAngle;
    private int mMaxMineCount;
    private float mExplosionRadius;
    private boolean mShooting;
    private List<PathSection> mSections;
    private List<Mine> mMines = new ArrayList<>();

    private Sprite.AnimatedInstance mSprite;

    private final Listener mMineListener = new Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            mMines.remove(obj);
            obj.removeListener(this);
        }
    };

    public MineLayer() {
        mAngle = getGame().getRandom(360f);
        mMaxMineCount = (int)getProperty("maxMineCount");
        mExplosionRadius = getProperty("explosionRadius");

        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldAnimated(Layers.TOWER_BASE);
        mSprite.setIndex(getGame().getRandom(4));
        mSprite.setListener(this);
        mSprite.setSequence(mSprite.sequenceForwardBackward());
        mSprite.setInterval(ANIMATION_DURATION);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.minelayer, 6);
        s.sprite.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);

        for (Mine m : mMines) {
            m.removeListener(mMineListener);
            m.remove();
        }

        mMines.clear();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            mSections = getPathSections();
        }
    }

    @Override
    public void enhance() {
        super.enhance();
        mMaxMineCount += getProperty("enhanceMaxMineCount");
        mExplosionRadius += getProperty("enhanceExplosionRadius");
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && mMines.size() < mMaxMineCount && mSections.size() > 0) {
            mShooting = true;
            setReloaded(false);
        }

        if (mShooting) {
            mSprite.tick();

            if (mSprite.getSequencePosition() == 5) {
                Mine m = new Mine(this, getPosition(), getTarget(), getDamage(), mExplosionRadius);
                m.addListener(mMineListener);
                mMines.add(m);
                getGame().add(m);

                mShooting = false;
            }
        }

        if (mSprite.getSequencePosition() != 0) {
            mSprite.tick();
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSprite.draw(canvas);
    }

    private Vector2 getTarget() {
        float totalLen = 0f;

        for (PathSection s : mSections) {
            totalLen += s.len;
        }

        float dist = getGame().getRandom(totalLen);

        for (PathSection s : mSections) {
            if (dist > s.len) {
                dist -= s.len;
            } else {
                Vector2 d = Vector2.fromTo(s.p1, s.p2);
                return d.norm().mul(dist).add(s.p1);
            }
        }

        return null;
    }
}
