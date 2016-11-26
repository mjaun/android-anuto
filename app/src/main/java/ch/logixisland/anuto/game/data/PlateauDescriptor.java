package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.game.entity.plateau.Plateau;

public class PlateauDescriptor {
    private final static String CLASS_PREFIX = "ch.logixisland.anuto.game.objects.impl.";

    /*
    ------ Fields ------
     */

    private Class<? extends Plateau> plateauClass;

    @Attribute(required=false)
    private float x;

    @Attribute(required=false)
    private float y;

    /*
    ------ Methods ------
     */

    @Attribute(name="clazz")
    private String getPlateauClassName() {
        return plateauClass.getName();
    }

    @Attribute(name="clazz")
    @SuppressWarnings("unchecked")
    private void setPlateauClassName(String className) throws ClassNotFoundException {
        plateauClass = (Class<? extends Plateau>) Class.forName(CLASS_PREFIX + className);
    }

    public Plateau createInstance() {
        Plateau p;

        try {
            p = plateauClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return p;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
