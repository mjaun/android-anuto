package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class CanonShotMG extends Shot {

    private final static float HIT_RANGE = 0.5f;
    private final static float MOVEMENT_SPEED = 8.0f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
    }

    private float mAngle;
    private float mDamage;

    private Sprite.FixedInstance mSprite;

    public CanonShotMG(GameObject origin, Vector2 position, Vector2 direction, float damage) {
        super(origin);
        setPosition(position);
        setSpeed(MOVEMENT_SPEED);
        setDirection(direction);

        mAngle = direction.angle();
        mDamage = damage;

        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldStatic(Layers.SHOT);
        mSprite.setListener(this);
        mSprite.setIndex(getGame().getRandom().nextInt(4));
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.canon_mg_shot, 4);
        s.sprite.setMatrix(0.2f, null, null, -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        Enemy enemy = (Enemy)getGame().get(Enemy.TYPE_ID)
                .filter(inRange(getPosition(), HIT_RANGE))
                .first();

        if (enemy != null) {
            enemy.damage(mDamage, getOrigin());
            this.remove();
        }

        if (!getGame().inGame(getPosition())) {
            this.remove();
        }
    }
}
