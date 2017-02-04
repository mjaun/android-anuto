package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteListener;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.entity.shot.GlueShot;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.vector.Line;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GlueTower extends Tower {

    private final static float SHOT_SPAWN_OFFSET = 0.8f;
    private final static float CANON_OFFSET_MAX = 0.5f;
    private final static float CANON_OFFSET_STEP = CANON_OFFSET_MAX / GameEngine.TARGET_FRAME_RATE / 0.8f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateTower;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private class SubCanon implements SpriteListener {
        float mAngle;
        StaticSprite mSprite;

        @Override
        public void draw(SpriteInstance sprite, Canvas canvas) {
            GlueTower.this.draw(sprite, canvas);

            canvas.rotate(mAngle);
            canvas.translate(mCanonOffset, 0);
        }
    }

    private float mGlueIntensity;
    private float mGlueDuration;
    private boolean mShooting;
    private float mCanonOffset;
    private SubCanon[] mCanons = new SubCanon[8];
    private List<Vector2> mTargets = new ArrayList<>();

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteTower;

    private final TickTimer mUpdateTimer = TickTimer.createInterval(0.1f);

    public GlueTower(TowerConfig config) {
        super(config);
        mGlueIntensity = getProperty("glueIntensity");
        mGlueDuration = getProperty("glueDuration");

        StaticData s = (StaticData) getStaticData();

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

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.attr.base4, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateTower = getSpriteFactory().createTemplate(R.attr.glueShot, 6);
        s.mSpriteTemplateTower.setMatrix(0.3f, 0.3f, null, null);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.attr.glueTowerGun, 4);
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
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && mUpdateTimer.tick() && !getPossibleTargets().isEmpty()) {
            mShooting = true;
            setReloaded(false);
        }

        if (mShooting) {
            mCanonOffset += CANON_OFFSET_STEP;

            if (mCanonOffset >= CANON_OFFSET_MAX) {
                mShooting = false;

                for (Vector2 target : mTargets) {
                    Vector2 position = Vector2.polar(SHOT_SPAWN_OFFSET, getAngleTo(target));
                    position.add(getPosition());

                    getGameEngine().add(new GlueShot(this, position, target, mGlueIntensity, mGlueDuration));
                }
            }
        } else if (mCanonOffset > 0f) {
            mCanonOffset -= CANON_OFFSET_STEP;
        }
    }

    @Override
    public void enhance() {
        super.enhance();
        mGlueIntensity += getProperty("enhanceGlueIntensity");
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }

    @Override
    public List<TowerProperty> getProperties() {
        List<TowerProperty> properties = new ArrayList<>();
        properties.add(new TowerProperty(R.string.intensity, mGlueIntensity));
        properties.add(new TowerProperty(R.string.duration, mGlueDuration));
        properties.add(new TowerProperty(R.string.reload, getReloadTime()));
        properties.add(new TowerProperty(R.string.range, getRange()));
        return properties;
    }

    private void determineTargets() {
        List<Line> sections = getPathSectionsInRange();
        float dist = 0f;

        mTargets.clear();

        for (Line sect : sections) {
            float angle = sect.angle();
            float length = sect.length();

            while (dist < length) {
                final Vector2 target = Vector2.polar(dist, angle).add(sect.getPoint1());

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

            dist -= length;
        }
    }
}
