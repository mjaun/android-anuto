package ch.logixisland.anuto.entity.plateau;

import ch.logixisland.anuto.engine.logic.Entity;
import ch.logixisland.anuto.engine.logic.EntityListener;
import ch.logixisland.anuto.engine.logic.GameEngine;
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

    public static Predicate<Plateau> occupiedBy(final Entity occupant) {
        return new Predicate<Plateau>() {
            @Override
            public boolean apply(Plateau value) {
                return value.mOccupant == occupant;
            }
        };
    }

    private Entity mOccupant;

    private final EntityListener mEntityListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity obj) {
            mOccupant = null;
        }
    };

    @Override
    public final int getEntityType() {
        return Types.PLATEAU;
    }

    public boolean isOccupied() {
        return mOccupant != null;
    }

    public void setOccupant(Entity occupant) {
        if (mOccupant != null) {
            mOccupant.removeListener(mEntityListener);
        }

        mOccupant = occupant;
        mOccupant.setPosition(getPosition());

        if (mOccupant != null) {
            mOccupant.addListener(mEntityListener);
        }
    }

}
