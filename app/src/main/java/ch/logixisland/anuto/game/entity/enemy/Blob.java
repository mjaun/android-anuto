package ch.logixisland.anuto.game.entity.enemy;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.render.AnimatedSprite;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.ReplicatedSprite;
import ch.logixisland.anuto.game.render.SpriteTemplate;

public class Blob extends Enemy {

    private final static float ANIMATION_SPEED = 1.5f;

    private class StaticData implements Runnable {
        SpriteTemplate mSpriteTemplate;
        AnimatedSprite mReferenceSprite;

        @Override
        public void run() {
            mReferenceSprite.tick();
        }
    }

    private ReplicatedSprite mSprite;

    public Blob() {
        StaticData s = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createReplication(s.mReferenceSprite);
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.blob, 9);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, null);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForward();
        s.mReferenceSprite.setFrequency(ANIMATION_SPEED);

        getGame().add(s);

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
