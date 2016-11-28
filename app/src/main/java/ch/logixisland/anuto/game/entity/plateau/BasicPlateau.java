package ch.logixisland.anuto.game.entity.plateau;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.Sprite;
import ch.logixisland.anuto.util.Random;

public class BasicPlateau extends Plateau {

    private class StaticData {
        public Sprite sprite;
    }

    private Sprite.FixedInstance mSprite;

    public BasicPlateau() {
        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldStatic(Layers.PLATEAU);
        mSprite.setIndex(Random.next(4));
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
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
