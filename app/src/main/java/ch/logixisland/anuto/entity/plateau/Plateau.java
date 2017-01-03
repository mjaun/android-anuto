package ch.logixisland.anuto.entity.plateau;

import org.simpleframework.xml.Root;

import ch.logixisland.anuto.entity.EntityListener;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.util.iterator.Predicate;

@Root
public abstract class Plateau extends Entity {

    public static Predicate<Plateau> unoccupied() {
        return new Predicate<Plateau>() {
            @Override
            public boolean apply(Plateau value) {
                return !value.isOccupied();
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
    public final int getType() {
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

        if (mOccupant != null) {
            mOccupant.addListener(mEntityListener);
        }
    }

}
