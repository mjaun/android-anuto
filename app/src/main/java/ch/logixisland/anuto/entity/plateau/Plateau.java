package ch.logixisland.anuto.entity.plateau;

import org.simpleframework.xml.Root;

import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;
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

    private Tower mOccupant;

    @Override
    public final int getType() {
        return Types.PLATEAU;
    }

    public boolean isOccupied() {
        return mOccupant != null;
    }

    public void setOccupant(Tower occupant) {
        mOccupant = occupant;
    }

}
