package ch.logixisland.anuto.engine.render.sprite;

import ch.logixisland.anuto.engine.render.DrawCommandBuffer;

public interface SpriteListener {
    void draw(SpriteInstance sprite, DrawCommandBuffer buffer);
}
