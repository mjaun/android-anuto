package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.util.RandomUtils;

public class BasicPlateau extends Plateau {

    private class StaticData {
        SpriteTemplate mSpriteTemplate;
    }

    private StaticSprite mSprite;

    public BasicPlateau() {
        StaticData s = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createStatic(Layers.PLATEAU, s.mSpriteTemplate);
        mSprite.setIndex(RandomUtils.next(4));
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.plateau1, 4);
        s.mSpriteTemplate.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);
    }
}
