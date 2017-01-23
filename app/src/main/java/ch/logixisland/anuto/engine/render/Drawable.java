package ch.logixisland.anuto.engine.render;

public interface Drawable {
    int getLayer();
    void draw(DrawCommandBuffer buffer);
}
