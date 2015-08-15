package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.TypeIds;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.Vector2;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.SHOT;

    /*
    ------ Members ------
     */

    protected float mSpeed;
    protected Vector2 mDirection;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled) {
            moveSpeed(mDirection, mSpeed);
        }
    }

    public StreamIterator<Enemy> getEncounteredEnemies(float shotWidth) {
        Vector2 nextPosition = mDirection.copy()
                .mul(mSpeed / GameEngine.TARGET_FRAME_RATE)
                .add(mPosition);

        return mGame.getGameObjects(TypeIds.ENEMY)
                .filter(GameObject.onLine(mPosition, nextPosition, shotWidth))
                .cast(Enemy.class);
    }
}
