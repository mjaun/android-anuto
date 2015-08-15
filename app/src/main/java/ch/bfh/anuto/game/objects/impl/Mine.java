package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.TypeIds;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.ParabolaFunction;
import ch.bfh.anuto.util.math.Vector2;

public class Mine extends Shot {

    public final static float TIME_TO_TARGET = 1.5f;

    private final static float ROTATION_RATE_MIN = 0.5f;
    private final static float ROTATION_RATE_MAX = 2.0f;

    private final static float HEIGHT_SCALING_START = 0.5f;
    private final static float HEIGHT_SCALING_STOP = 1.0f;
    private final static float HEIGHT_SCALING_PEAK = 1.5f;

    private boolean mFlying = true;
    private float mAngle;
    private float mRotationStep;
    private int mTicksToTarget;
    private final ParabolaFunction mHeightScalingFunction;

    private final Sprite mSprite;

    public Mine(Vector2 position, Vector2 target) {
        setPosition(position);

        mSpeed = getDistanceTo(target) / TIME_TO_TARGET;
        mDirection = getDirectionTo(target);
        mTicksToTarget = Math.round(GameEngine.TARGET_FRAME_RATE * TIME_TO_TARGET);

        mRotationStep = mGame.getRandom(ROTATION_RATE_MIN, ROTATION_RATE_MAX) * 360f / GameEngine.TARGET_FRAME_RATE;

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.mortar_shot, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom().nextInt(4));
        mSprite.setMatrix(0.7f, 0.7f, null, null);
        mSprite.setLayer(Layers.SHOT);

        mHeightScalingFunction = new ParabolaFunction();
        mHeightScalingFunction.setProperties(HEIGHT_SCALING_START, HEIGHT_SCALING_STOP, HEIGHT_SCALING_PEAK);
        mHeightScalingFunction.setSection(GameEngine.TARGET_FRAME_RATE * TIME_TO_TARGET);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSprite);
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

        if (mFlying) {
            mAngle += mRotationStep;
            mHeightScalingFunction.step();
            mTicksToTarget--;

            if (mTicksToTarget <= 0) {
                mFlying = false;
                mHeightScalingFunction.reset();
                mSpeed = 0f;
            }
        } else if (mGame.getTimer100ms().tick()) {
            StreamIterator<Enemy> enemiesInRange = mGame.getGameObjects(TypeIds.ENEMY)
                    .filter(GameObject.inRange(mPosition, 0.5f))
                    .cast(Enemy.class);

            if (enemiesInRange.hasNext()) {
                mGame.add(new ExplosionEffect(mPosition));
                this.remove();
            }
        }
    }
}
