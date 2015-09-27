package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueTower extends Tower {

    private final static float SHOT_SPAWN_OFFSET = 0.8f;

    private final static float CANON_OFFSET_MAX = 0.5f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteTower;
        public Sprite spriteCanon;
    }

    private class SubCanon implements Sprite.Listener {
        public float angle;
        public Sprite.FixedInstance sprite;

        @Override
        public void onDraw(DrawObject sprite, Canvas canvas) {
            GlueTower.this.onDraw(sprite, canvas);

            canvas.rotate(angle);
            canvas.translate(mCanonOffset, 0);
        }
    }

    private float mGlueDuration;
    private boolean mShooting;
    private float mCanonOffset;
    private SubCanon[] mCanons = new SubCanon[8];
    private List<Vector2> mTargets = new ArrayList<>();

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.FixedInstance mSpriteTower;

    public GlueTower() {
        mGlueDuration = getProperty("glueDuration");

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom().nextInt(4));

        mSpriteTower = s.spriteTower.yieldStatic(Layers.TOWER_UPPER);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(getGame().getRandom().nextInt(6));

        for (int i = 0; i < mCanons.length; i++) {
            SubCanon c = new SubCanon();
            c.angle = 360f / mCanons.length * i;
            c.sprite = s.spriteCanon.yieldStatic(Layers.TOWER_LOWER);
            c.sprite.setListener(c);
            mCanons[i] = c;
        }
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base4, 4);
        s.spriteBase.setMatrix(1f, 1f, null, null);

        s.spriteTower = Sprite.fromResources(R.drawable.glue_shot, 6);
        s.spriteTower.setMatrix(0.3f, 0.3f, null, null);

        s.spriteCanon = Sprite.fromResources(R.drawable.glue_tower_gun, 4);
        s.spriteCanon.setMatrix(0.3f, 0.4f, null, -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSpriteBase);
        getGame().add(mSpriteTower);

        for (SubCanon c : mCanons) {
            getGame().add(c.sprite);
        }
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSpriteBase);
        getGame().remove(mSpriteTower);

        for (SubCanon c : mCanons) {
            getGame().remove(c.sprite);
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
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && getGame().tick100ms(this) && !getPossibleTargets().isEmpty()) {
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

                    getGame().add(new GlueShot(this, position, target, 1f / getDamage(), mGlueDuration));
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
