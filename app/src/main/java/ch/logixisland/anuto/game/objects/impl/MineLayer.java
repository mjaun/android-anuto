package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.math.Vector2;

public class MineLayer extends Tower {

    private final static int MAX_MINE_COUNT = 3;

    private final static float ANIMATION_DURATION = 0.5f;

    private float mAngle;
    private boolean mShooting;
    private List<Mine> mMines = new ArrayList<>();
    private List<PathSection> mSections;

    private Sprite mSprite;
    private Sprite.Animator mAnimator;

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
        mAngle = 90f;

        mSprite = Sprite.fromResources(getGame().getResources(), R.drawable.minelayer, 6);
        mSprite.setListener(this);
        mSprite.setMatrix(1f, 1f, null, null);
        mSprite.setLayer(Layers.TOWER);

        mAnimator = new Sprite.Animator();
        mAnimator.setSequence(mSprite.sequenceForwardBackward());
        mAnimator.setInterval(ANIMATION_DURATION);
        mSprite.setAnimator(mAnimator);
    }

    @Override
    public void init() {
        super.init();

        mAngle = getGame().getRandom(360f);
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
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && mMines.size() < MAX_MINE_COUNT && mSections.size() > 0) {
            mShooting = true;
            setReloaded(false);
        }

        if (mShooting) {
            mSprite.animate();

            if (mAnimator.getPosition() == 5) {
                Mine m = new Mine(getPosition(), getTarget(), getDamage());
                m.addListener(mMineListener);
                mMines.add(m);
                getGame().add(m);

                mShooting = false;
            }
        }

        if (mAnimator.getPosition() != 0) {
            mSprite.animate();
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
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
