package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.engine.logic.EntityDependencies;
import ch.logixisland.anuto.util.GenericFactory;

public class PlateauFactory {

    private final EntityDependencies mDependencies;
    private final GenericFactory<Plateau> mFactory;

    public PlateauFactory(EntityDependencies dependencies) {
        mDependencies = dependencies;
        mFactory = new GenericFactory<>(EntityDependencies.class);
        mFactory.registerClass(BasicPlateau.class);
    }

    public Plateau createPlateau(String name) {
        return mFactory.createInstance(name, mDependencies);
    }

}
