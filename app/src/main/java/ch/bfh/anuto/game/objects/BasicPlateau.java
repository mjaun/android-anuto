package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;

public class BasicPlateau extends Plateau {
    public BasicPlateau() {

    }

    public BasicPlateau(PointF position) {
        setPosition(position);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_plateau);
    }
}
