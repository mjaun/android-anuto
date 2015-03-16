package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.Plateau;

public class BasicPlateau extends Plateau {
    public BasicPlateau() {

    }

    public BasicPlateau(PointF position) {
        setPosition(position);
    }

    @Override
    public void initResources(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_plateau);
    }
}
