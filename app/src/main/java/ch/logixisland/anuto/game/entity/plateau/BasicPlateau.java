package ch.logixisland.anuto.game.entity.plateau;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.Random;

public class BasicPlateau extends Plateau {

    private class StaticData {
        SpriteTemplate mSpriteTemplate;
    }

    private StaticSprite mSprite;

    public BasicPlateau() {
        StaticData s = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createStatic(Layers.PLATEAU, s.mSpriteTemplate);
        mSprite.setIndex(Random.next(4));
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.plateau1, 4);
        s.mSpriteTemplate.setMatrix(1f, 1f, null, null);

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
