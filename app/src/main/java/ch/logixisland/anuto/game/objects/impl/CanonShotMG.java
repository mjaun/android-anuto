package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class CanonShotMG extends Shot {

    private final static float DAMAGE = 60f;
    private final static float MOVEMENT_SPEED = 8.0f;

    private float mAngle;
    private float mDamage;

    private final Sprite mSprite;

    public CanonShotMG(Vector2 position, Vector2 direction) {
        setPosition(position);

        mSpeed = MOVEMENT_SPEED;
        mDirection = direction;
        mAngle = mDirection.angle();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.canon_mg_shot, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom().nextInt(4));
        mSprite.setMatrix(0.2f, null, null, -90f);
        mSprite.setLayer(Layers.SHOT);
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

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        StreamIterator<Enemy> encountered = mGame.get(Enemy.TYPE_ID)
                .filter(GameObject.inRange(mPosition, 0.5f))
                .cast(Enemy.class);

        if (encountered.hasNext()) {
            Enemy enemy = encountered.next();
            encountered.close();

            enemy.damage(DAMAGE);
            this.remove();
        }

        if (!mGame.inGame(mPosition)) {
            this.remove();
        }
    }
}
