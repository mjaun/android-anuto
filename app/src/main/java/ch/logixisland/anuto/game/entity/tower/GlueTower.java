package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.shot.GlueShot;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.sprite.SpriteInstance;
import ch.logixisland.anuto.game.render.sprite.SpriteListener;
import ch.logixisland.anuto.game.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.game.render.sprite.StaticSprite;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GlueTower extends Tower {

    private final static float SHOT_SPAWN_OFFSET = 0.8f;

    private final static float CANON_OFFSET_MAX = 0.5f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateTower;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private class SubCanon implements SpriteListener {
        float mAngle;
        StaticSprite mSprite;

        @Override
        public void onDraw(SpriteInstance sprite, Canvas canvas) {
            GlueTower.this.onDraw(sprite, canvas);

            canvas.rotate(mAngle);
            canvas.translate(mCanonOffset, 0);
        }
    }

    private float mGlueDuration;
    private boolean mShooting;
    private float mCanonOffset;
    private SubCanon[] mCanons = new SubCanon[8];
    private List<Vector2> mTargets = new ArrayList<>();

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteTower;

    public GlueTower() {
        mGlueDuration = getProperty("glueDuration");

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateBase);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(RandomUtils.next(4));

        mSpriteTower = getSpriteFactory().createStatic(Layers.TOWER_UPPER, s.mSpriteTemplateTower);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(RandomUtils.next(6));

        for (int i = 0; i < mCanons.length; i++) {
            SubCanon c = new SubCanon();
            c.mAngle = 360f / mCanons.length * i;
            c.mSprite = getSpriteFactory().createStatic(Layers.TOWER_LOWER, s.mSpriteTemplateCanon);
            c.mSprite.setListener(c);
            mCanons[i] = c;
        }
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.drawable.base4, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateTower = getSpriteFactory().createTemplate(R.drawable.glue_shot, 6);
        s.mSpriteTemplateTower.setMatrix(0.3f, 0.3f, null, null);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.drawable.glue_tower_gun, 4);
        s.mSpriteTemplateCanon.setMatrix(0.3f, 0.4f, null, -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSpriteBase);
        getGameEngine().add(mSpriteTower);

        for (SubCanon c : mCanons) {
            getGameEngine().add(c.mSprite);
        }
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSpriteBase);
        getGameEngine().remove(mSpriteTower);

        for (SubCanon c : mCanons) {
            getGameEngine().remove(c.mSprite);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            determineTargets();
        }
    }

    @Override
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && getGameEngine().tick100ms(this) && !getPossibleTargets().isEmpty()) {
            mShooting = true;
            setReloaded(false);
        }

        if (mShooting) {
            mCanonOffset += 0.01f;

            if (mCanonOffset >= CANON_OFFSET_MAX) {
                mShooting = false;

                for (Vector2 target : mTargets) {
                    Vector2 position = Vector2.polar(SHOT_SPAWN_OFFSET, getAngleTo(target));
                    position.add(getPosition());

                    getGameEngine().add(new GlueShot(this, position, target, 1f / getDamage(), mGlueDuration));
                }
            }
        } else if (mCanonOffset > 0f) {
            mCanonOffset -= 0.01f;
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }

    private void determineTargets() {
        List<PathSection> sections = getPathSections();
        float dist = 0f;

        mTargets.clear();

        for (PathSection sect : sections) {
            float angle = Vector2.fromTo(sect.p1, sect.p2).angle();

            while (dist < sect.len) {
                final Vector2 target = Vector2.polar(dist, angle).add(sect.p1);

                boolean free = StreamIterator.fromIterable(mTargets)
                        .filter(new Predicate<Vector2>() {
                            @Override
                            public boolean apply(Vector2 value) {
                                return Vector2.fromTo(value, target).len() < 0.5f;
                            }
                        })
                        .isEmpty();

                if (free) {
                    mTargets.add(target);
                }

                dist += 1f;
            }

            dist -= sect.len;
        }
    }
}
