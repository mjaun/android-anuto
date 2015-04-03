package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Plateau;

public class BasicPlateau extends Plateau {

    private Sprite mSprite;

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(this, res, R.drawable.basic_plateau);
        mGame.addDrawObject(mSprite, Layers.PLATEAU);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mSprite);
    }
}
