package ch.logixisland.anuto.data.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TowerSlots {

    @Element(name = "slot1")
    private String mSlot1;

    @Element(name = "slot2")
    private String mSlot2;

    @Element(name = "slot3")
    private String mSlot3;

    @Element(name = "slot4")
    private String mSlot4;

    public String getSlot1() {
        return mSlot1;
    }

    public String getSlot2() {
        return mSlot2;
    }

    public String getSlot3() {
        return mSlot3;
    }

    public String getSlot4() {
        return mSlot4;
    }

}
