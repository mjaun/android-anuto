package ch.bfh.anuto.game.objects;

import org.simpleframework.xml.Root;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.TypeIds;
import ch.bfh.anuto.util.iterator.Predicate;

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

    protected Tower mOccupant;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {

    }

    public boolean isOccupied() {
        return mOccupant != null;
    }

    public void setOccupant(Tower occupant) {
        mOccupant = occupant;
    }
}
