package ch.logixisland.anuto.data.map;

public class MapInfo {

    private String mMapId;
    private int mMapNameResId;
    private int mMapDescriptorResId;

    MapInfo(String mapId, int mapNameResId, int mapDescriptorResId) {
        mMapId = mapId;
        mMapNameResId = mapNameResId;
        mMapDescriptorResId = mapDescriptorResId;
    }

    public String getMapId() {
        return mMapId;
    }

    public int getMapNameResId() {
        return mMapNameResId;
    }

    public int getMapDescriptorResId() {
        return mMapDescriptorResId;
    }
}
