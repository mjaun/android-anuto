package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Plateau;

public class BasicPlateau extends Plateau {

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_plateau);
    }
}
