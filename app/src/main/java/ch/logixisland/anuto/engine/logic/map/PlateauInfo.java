package ch.logixisland.anuto.engine.logic.map;

import ch.logixisland.anuto.util.container.KeyValueStore;
import ch.logixisland.anuto.util.math.Vector2;

public class PlateauInfo {

    private final String mName;
    private final Vector2 mPosition;

    public PlateauInfo(KeyValueStore data) {
        mName = data.getString("name");
        mPosition = new Vector2(
                data.getFloat("x"),
                data.getFloat("y")
        );
    }

    public String getName() {
        return mName;
    }

    public Vector2 getPosition() {
        return mPosition;
    }

}
