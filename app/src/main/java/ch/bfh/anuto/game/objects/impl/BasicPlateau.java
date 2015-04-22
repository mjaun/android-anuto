package ch.bfh.anuto.game.objects.impl;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.game.objects.Plateau;

public class BasicPlateau extends Plateau {

    private Sprite mSprite;

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.basic_plateau);
        mSprite.setListener(this);
        mSprite.setLayer(Layers.PLATEAU);
        mGame.add(mSprite);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }
}
