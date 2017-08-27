package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.util.iterator.Predicate;

public abstract class Plateau extends Entity {

    Plateau(GameEngine gameEngine) {
        super(gameEngine);
    }

    public static Predicate<Plateau> unoccupied() {
        return new Predicate<Plateau>() {
            @Override
            public boolean apply(Plateau value) {
                return !value.isOccupied();
            }
        };
    }

    private boolean mOccupied;

    @Override
    public final int getEntityType() {
        return Types.PLATEAU;
    }

    public boolean isOccupied() {
        return mOccupied;
    }

    public void setOccupied(boolean occupied) {
        mOccupied = occupied;
    }

}
