package ch.logixisland.anuto.entity.tower;

public class TowerProperty {

    private final int mTextId;
    private final float mValue;

    public TowerProperty(int textId, float value) {
        mTextId = textId;
        mValue = value;
    }

    public int getTextId() {
        return mTextId;
    }

    public float getValue() {
        return mValue;
    }

}
