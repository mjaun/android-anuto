package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.engine.render.shape.ShapeFactory;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.sound.SoundFactory;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;

public interface EntityDependencies {
    GameEngine getGameEngine();
    ShapeFactory getShapeFactory();
    SpriteFactory getSpriteFactory();
    SoundFactory getSoundFactory();
    GameSettings getGameSettings();
    LevelDescriptor getLevelDescriptor();
}
