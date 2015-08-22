package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.game.objects.Plateau;

public class PlateauDescriptor {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.objects.impl.";

    public Class<? extends Plateau> clazz;

    @Attribute(required=false)
    public float x;

    @Attribute(required=false)
    public float y;

    @Attribute(name="clazz")
    private String getClazz() {
        return clazz.getName();
    }

    @Attribute(name="clazz")
    private void setClazz(String className) throws ClassNotFoundException {
        clazz = (Class<? extends Plateau>) Class.forName(CLASS_PREFIX + className);
    }

    public Plateau create() {
        Plateau p;

        try {
            p = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return p;
    }
}
