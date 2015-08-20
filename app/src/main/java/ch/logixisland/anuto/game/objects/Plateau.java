package ch.logixisland.anuto.game.objects;

import org.simpleframework.xml.Root;

import ch.logixisland.anuto.game.TypeIds;
import ch.logixisland.anuto.util.iterator.Predicate;

@Root
public abstract class Plateau extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.PLATEAU;

    /*
    ------ Static ------
     */

    public static Predicate<Plateau> unoccupied() {
        return new Predicate<Plateau>() {
            @Override
            public boolean apply(Plateau value) {
                return !value.isOccupied();
            }
        };
    }

    /*
    ------ Members ------
     */

    private Tower mOccupant;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }


    public boolean isOccupied() {
        return mOccupant != null;
    }

    public void setOccupant(Tower occupant) {
        mOccupant = occupant;
    }
}
