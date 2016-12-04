package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.shot.Mine;
import ch.logixisland.anuto.game.render.AnimatedSprite;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class MineLayer extends Tower {

    private final static float ANIMATION_DURATION = 1f;

    private class StaticData {
        public SpriteTemplate mSpriteTemplate;
    }

    private float mAngle;
    private int mMaxMineCount;
    private float mExplosionRadius;
    private boolean mShooting;
    private List<PathSection> mSections;
    private List<Mine> mMines = new ArrayList<>();

    private AnimatedSprite mSprite;

    private final Listener mMineListener = new Listener() {
        @Override
        public void onObjectAdded(Entity obj) {

        }

        @Override
        public void onObjectRemoved(Entity obj) {
            mMines.remove(obj);
            obj.removeListener(this);
        }
    };

    public MineLayer() {
        mAngle = RandomUtils.next(360f);
        mMaxMineCount = (int)getProperty("maxMineCount");
        mExplosionRadius = getProperty("explosionRadius");

        StaticData s = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createAnimated(Layers.TOWER_BASE, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setSequenceForwardBackward();
        mSprite.setInterval(ANIMATION_DURATION);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.minelayer, 6);
        s.mSpriteTemplate.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);

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
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
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

            if (mSprite.getSequenceIndex() == 5) {
                Mine m = new Mine(this, getPosition(), getTarget(), getDamage(), mExplosionRadius);
                m.addListener(mMineListener);
                mMines.add(m);
                getGameEngine().add(m);

                mShooting = false;
            }
        }

        if (mSprite.getSequenceIndex() != 0) {
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

        float dist = RandomUtils.next(totalLen);

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
