package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;

@Root
public class TowerSlots {

    @ElementMap(entry = "slot", key = "index", attribute = true, inline = true)
    private Map<Integer, String> mSlotMap = new HashMap<>();

    public String getTowerOfSlot(int slot) {
        if (!mSlotMap.containsKey(slot)) {
            return null;
        }

        return mSlotMap.get(slot);
    }

}
