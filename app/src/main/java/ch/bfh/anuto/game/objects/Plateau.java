package ch.bfh.anuto.game.objects;

import org.simpleframework.xml.Root;

import ch.bfh.anuto.game.GameObject;

@Root
public abstract class Plateau extends GameObject {
    public static final int TYPEID = 1;

    @Override
    public int getTypeId() {
        return TYPEID;
    }
}
