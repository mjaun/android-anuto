package ch.logixisland.anuto.business.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.R;

public class MapRepository {

    private final List<MapInfo> mMapInfos;

    public MapRepository() {
        mMapInfos = new ArrayList<>();
        mMapInfos.add(new MapInfo("original", R.string.map_original_name, R.raw.map_original));
        mMapInfos.add(new MapInfo("waiting_line", R.string.map_waiting_line_name, R.raw.map_waiting_line));
        mMapInfos.add(new MapInfo("turn_round", R.string.map_turn_round_name, R.raw.map_turn_round));
        mMapInfos.add(new MapInfo("hurry", R.string.map_hurry_name, R.raw.map_hurry));
        mMapInfos.add(new MapInfo("civyshk_yard", R.string.map_civyshk_yard_name, R.raw.map_civyshk_yard));
        mMapInfos.add(new MapInfo("civyshk_2y", R.string.map_civyshk_2y_name, R.raw.map_civyshk_2y));
        mMapInfos.add(new MapInfo("civyshk_line5", R.string.map_civyshk_line5_name, R.raw.map_civyshk_line5));
        mMapInfos.add(new MapInfo("civyshk_labyrinth", R.string.map_civyshk_labyrinth_name, R.raw.map_civyshk_labyrinth));
    }

    public List<MapInfo> getMapInfos() {
        return Collections.unmodifiableList(mMapInfos);
    }

    public MapInfo getMapById(String mapId) {
        for (MapInfo mapInfo : mMapInfos) {
            if (mapInfo.getMapId().equals(mapId)) {
                return mapInfo;
            }
        }

        throw new RuntimeException("Map not found!");
    }

    public String getDefaultMapId() {
        return "original";
    }
}
