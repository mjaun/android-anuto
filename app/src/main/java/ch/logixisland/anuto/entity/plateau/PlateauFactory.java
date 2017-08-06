package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class PlateauFactory {

    private final GameEngine mGameEngine;

    public PlateauFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public Plateau createPlateau(String name) {
        switch (name) {
            case "basic":
                return new BasicPlateau(mGameEngine);
            default:
                throw new IllegalArgumentException("Unknown plateau name!");
        }
    }

}
