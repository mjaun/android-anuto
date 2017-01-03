package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.PlateauDescriptor;

public class PlateauFactory {

    private final GenericFactory<Plateau> mFactory = new GenericFactory<>(Plateau.class);

    public Plateau createPlateau(String name) {
        return mFactory.createInstance(name);
    }

}
