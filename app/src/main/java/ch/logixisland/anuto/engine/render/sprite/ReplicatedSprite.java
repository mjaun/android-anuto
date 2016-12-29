package ch.logixisland.anuto.engine.render.sprite;

public class ReplicatedSprite extends SpriteInstance {

    private final SpriteInstance mOriginal;

    ReplicatedSprite(SpriteInstance original) {
        super(original.getLayer(), original.getTemplate());
        mOriginal = original;
    }

    @Override
    int getIndex() {
        return mOriginal.getIndex();
    }
}
