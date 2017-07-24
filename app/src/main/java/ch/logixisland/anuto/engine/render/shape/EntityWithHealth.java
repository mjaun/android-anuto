package ch.logixisland.anuto.engine.render.shape;

import ch.logixisland.anuto.util.math.Vector2;

public interface EntityWithHealth {
    Vector2 getPosition();
    float getHealth();
    float getMaxHealth();
}
