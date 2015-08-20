package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueTower extends Tower {

    private final static float SHOT_SPAWN_OFFSET = 0.8f;

    private final static float CANON_OFFSET_MAX = 0.5f;

    private class SubCanon implements Sprite.Listener {
        public float angle;
        public Sprite sprite;

        @Override
        public void onDraw(Sprite sprite, Canvas canvas) {
            GlueTower.this.onDraw(sprite, canvas);

            canvas.rotate(angle);
            canvas.translate(mCanonOffset, 0);
        }
    }

    private boolean mShooting;
    private float mCanonOffset;
    private SubCanon[] mCanons = new SubCanon[8];
    private List<Vector2> mTargets = new ArrayList<Vector2>();

    private final Sprite mSpriteBase;
    private final Sprite mSpriteTower;

    public GlueTower() {
        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base4, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.glue_shot, 6);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(mGame.getRandom().nextInt(6));
        mSpriteTower.setMatrix(0.3f, 0.3f, null, null);
        mSpriteTower.setLayer(Layers.TOWER_UPPER);

        for (int i = 0; i < mCanons.length; i++) {
            mCanons[i] = new SubCanon();
            mCanons[i].angle = 360f / mCanons.length * i;

            Sprite s = Sprite.fromResources(mGame.getResources(), R.drawable.glue_tower_gun, 4);
            s.setListener(mCanons[i]);
            s.setIndex(mGame.getRandom().nextInt(4));
            s.setMatrix(0.3f, 0.4f, null, -90f);
            s.setLayer(Layers.TOWER_LOWER);
            mCanons[i].sprite = s;
        }
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSpriteBase);
        mGame.add(mSpriteTower);
        
        for (SubCanon c : mCanons) {
            mGame.add(c.sprite);
        }
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteTower);

        for (SubCanon c : mCanons) {
            mGame.remove(c.sprite);
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
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloaded) {
            mShooting = true;
            mReloaded = false;
        }

        if (mShooting) {
            mCanonOffset += 0.01f;

            if (mCanonOffset >= CANON_OFFSET_MAX) {
                mShooting = false;

                for (Vector2 target : mTargets) {
                    Vector2 position = Vector2.polar(SHOT_SPAWN_OFFSET, getAngleTo(target));
                    position.add(mPosition);

                    mGame.add(new GlueShot(position, target, 1f / getDamage()));
                }
            }
        } else if (mCanonOffset > 0f) {
            mCanonOffset -= 0.01f;
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
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

                boolean free = StreamIterator.fromIterator(mTargets.iterator())
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
