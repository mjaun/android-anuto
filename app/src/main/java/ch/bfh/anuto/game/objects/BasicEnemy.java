package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Path;
import ch.bfh.anuto.game.Sprite;

public class BasicEnemy extends Enemy {
    private final static float MOVEMENT_SPEED = 1.5f / GameEngine.TARGET_FPS;
    private final static float ANIMATION_SPEED = 1f / GameEngine.TARGET_FPS;

    public BasicEnemy() {
    }

    public BasicEnemy(PointF position, Path path) {
        setPosition(position);
        setPath(path);
    }

    @Override
    public void tick() {
        if (!hasWayPoint()) {
            return;
        }

        if (getDistanceToWayPoint() < MOVEMENT_SPEED) {
            setPosition(getWayPoint());
            nextWayPoint();
        }
        else {
            move(getDirectionToWayPoint(), MOVEMENT_SPEED);
        }

        mSprite.cycle2(ANIMATION_SPEED);
    }

    @Override
    public void initResources(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_enemy, 12);
        mSprite.getMatrix().postScale(0.9f, 0.9f);
    }
}
