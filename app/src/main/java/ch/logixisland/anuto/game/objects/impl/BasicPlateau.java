package ch.logixisland.anuto.game.objects.impl;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Plateau;
import ch.logixisland.anuto.game.objects.Sprite;

public class BasicPlateau extends Plateau {

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
    }

    private Sprite.FixedInstance mSprite;

    public BasicPlateau() {
        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldStatic(Layers.PLATEAU);
        mSprite.setIndex(getGame().getRandom(4));
        mSprite.setListener(this);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.plateau1, 4);
        s.sprite.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);
    }
}
