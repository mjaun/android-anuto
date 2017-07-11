package ch.logixisland.anuto.entity.plateau;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.EntityDependencies;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteListener;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.util.RandomUtils;

public class BasicPlateau extends Plateau implements SpriteListener {

    private class StaticData {

        SpriteTemplate mSpriteTemplate;
    }
    private StaticSprite mSprite;

    public BasicPlateau(EntityDependencies dependencies) {
        super(dependencies);
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

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        canvas.translate(getPosition().x(), getPosition().y());
    }
}
