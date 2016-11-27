package ch.logixisland.anuto.game.entity.plateau;

import org.simpleframework.xml.Root;

import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.util.iterator.Predicate;

@Root
public abstract class Plateau extends Entity {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = Types.PLATEAU;

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
    public final int getType() {
        return TYPE_ID;
    }


    public boolean isOccupied() {
        return mOccupant != null;
    }

    public void setOccupant(Tower occupant) {
        mOccupant = occupant;
    }
}
