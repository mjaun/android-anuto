package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Plateau;

public class BasicPlateau extends Plateau {

    private Sprite mSprite;

    @Override
    public void init() {
        super.init();
        mSprite = Sprite.fromResources(this, R.drawable.basic_plateau);
        mSprite.setLayer(Layers.PLATEAU);
        mGame.add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();
        mGame.remove(mSprite);
    }
}
