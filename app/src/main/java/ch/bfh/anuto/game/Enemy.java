package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Enemy extends GameObject {
    public Enemy(Game game, PointF position) {
        super(game, position);
    }
}
