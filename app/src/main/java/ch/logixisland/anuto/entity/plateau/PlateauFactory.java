package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.GenericFactory;

public class PlateauFactory {

    private final GameEngine mGameEngine;
    private final GenericFactory<Plateau> mFactory;

    public PlateauFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
        mFactory = new GenericFactory<>(GameEngine.class);
        mFactory.registerClass(BasicPlateau.class);
    }

    public Plateau createPlateau(String name) {
        return mFactory.createInstance(name, mGameEngine);
    }

}
