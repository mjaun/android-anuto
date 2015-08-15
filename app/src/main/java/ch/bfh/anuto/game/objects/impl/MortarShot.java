package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.ParabolaFunction;
import ch.bfh.anuto.util.math.Vector2;

public class MortarShot extends Shot {

    public final static float TIME_TO_TARGET = 1.5f;

    private final static float ROTATION_RATE_MIN = 0.5f;
    private final static float ROTATION_RATE_MAX = 2.0f;

    private final static float HEIGHT_SCALING_START = 0.5f;
    private final static float HEIGHT_SCALING_STOP = 1.0f;
    private final static float HEIGHT_SCALING_PEAK = 1.5f;

    private float mAngle;
    private float mRotationStep;
    private ParabolaFunction mHeightScalingFunction;

    private Sprite mSprite;

    public MortarShot(Vector2 position, Vector2 target) {
        setPosition(position);

        mSpeed = getDistanceTo(target) / TIME_TO_TARGET;
        mDirection = getDirectionTo(target);
    }

    @Override
    public void init() {
        super.init();

        mAngle = mGame.getRandom(360f);
        mRotationStep = mGame.getRandom(ROTATION_RATE_MIN, ROTATION_RATE_MAX) * 360f / GameEngine.TARGET_FRAME_RATE;

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.catapult_shot, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom().nextInt(4));
        mSprite.setMatrix(0.7f, 0.7f, null, null);
        mSprite.setLayer(Layers.SHOT);
        mGame.add(mSprite);

        mHeightScalingFunction = new ParabolaFunction();
        mHeightScalingFunction.setProperties(HEIGHT_SCALING_START, HEIGHT_SCALING_STOP, HEIGHT_SCALING_PEAK);
        mHeightScalingFunction.setSection(GameEngine.TARGET_FRAME_RATE * TIME_TO_TARGET);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        float s = mHeightScalingFunction.getValue();
        canvas.scale(s, s);
        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        mAngle += mRotationStep;

        if (mHeightScalingFunction.step()) {
            mGame.add(new ExplosionEffect(mPosition));
            this.remove();
        }
    }
}
