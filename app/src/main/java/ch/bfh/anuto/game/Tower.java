package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Tower extends GameObject {
    protected Enemy nextEnemy() {
        // TODO: handle range and different selection strategies

        for (GameObject obj : mGame.getObjects()) {
            if (obj instanceof Enemy) {
                return (Enemy)obj;
            }
        }

        return null;
    }
}
