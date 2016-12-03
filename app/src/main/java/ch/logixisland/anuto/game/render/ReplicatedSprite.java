package ch.logixisland.anuto.game.render;

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
