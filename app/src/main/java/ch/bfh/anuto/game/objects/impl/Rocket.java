package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.objects.HomingShot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class Rocket extends HomingShot {

    private final static float MOVEMENT_SPEED = 2.5f;
    private final static float ANIMATION_SPEED = 3f;

    private Sprite mSprite;
    private Sprite mSpriteFire;
    private float mAngle = 0f;

    public Rocket() {
        mSpeed = MOVEMENT_SPEED;
    }

    public Rocket(Vector2 position) {
        this();

        setPosition(position);
        mEnabled = false;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    @Override
    public void init() {
        super.init();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.rocket_shot, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom(4));
        mSprite.setMatrix(0.8f, 1f, null, -90f);
        mSprite.setLayer(Layers.SHOT);
        mGame.add(mSprite);

        mSpriteFire = Sprite.fromResources(mGame.getResources(), R.drawable.rocket_fire, 4);
        mSpriteFire.setListener(this);
        mSpriteFire.setMatrix(0.3f, 0.3f, new Vector2(0.15f, 0.6f), -90f);
        mSpriteFire.setLayer(Layers.SHOT_LOWER);

        if (mEnabled) {
            mGame.add(mSpriteFire);
        }

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSpriteFire.sequenceForward());
        animator.setFrequency(ANIMATION_SPEED);
        mSpriteFire.setAnimator(animator);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);

        if (mEnabled) {
            mGame.remove(mSpriteFire);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (isInGame() && !enabled) {
            mGame.remove(mSpriteFire);
        }

        if (isInGame() && enabled) {
            mGame.add(mSpriteFire);
        }
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        if (mEnabled) {
            mDirection = getDirectionTo(mTarget);
            mAngle = mDirection.angle();

            mSpriteFire.animate();
        }

        super.tick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy)mGame.getGameObjects(Enemy.TYPE_ID)
                .min(GameObject.distanceTo(mPosition));

        if (closest == null) {
            mGame.remove(this);
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        mGame.add(new Explosion(mTarget.getPosition()));
        mGame.remove(this);
    }
}
