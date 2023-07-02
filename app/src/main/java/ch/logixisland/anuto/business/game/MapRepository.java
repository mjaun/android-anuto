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
        mMapInfos.add(new MapInfo("higgledy_piggledy", R.string.map_higgledy_piggledy_name, R.raw.map_higgledy_piggledy));
        mMapInfos.add(new MapInfo("big_u", R.string.map_big_u_name, R.raw.map_big_u));
        mMapInfos.add(new MapInfo("cloverleaf", R.string.map_cloverleaf_name, R.raw.map_cloverleaf));
        mMapInfos.add(new MapInfo("roundabout", R.string.map_roundabout_name, R.raw.map_roundabout));
        mMapInfos.add(new MapInfo("runway", R.string.map_runway_name, R.raw.map_runway));
        mMapInfos.add(new MapInfo("wtf", R.string.map_wtf_name, R.raw.map_wtf));
        mMapInfos.add(new MapInfo("turn_left", R.string.map_turn_left_name, R.raw.map_turn_left));
        mMapInfos.add(new MapInfo("turn_right", R.string.map_turn_right_name, R.raw.map_turn_right));
        mMapInfos.add(new MapInfo("oddball", R.string.map_oddball_name, R.raw.map_oddball));
        mMapInfos.add(new MapInfo("spiral1", R.string.map_spiral1_name, R.raw.map_spiral1));
        mMapInfos.add(new MapInfo("chaos", R.string.map_chaos_name, R.raw.map_chaos));
        mMapInfos.add(new MapInfo("moar_chaos", R.string.map_moar_chaos_name, R.raw.map_moar_chaos));
        mMapInfos.add(new MapInfo("spiral2", R.string.map_spiral2_name, R.raw.map_spiral2));
        mMapInfos.add(new MapInfo("nou", R.string.map_nou_name, R.raw.map_nou));
        mMapInfos.add(new MapInfo("highscore", R.string.map_highscore_name, R.raw.map_highscore));
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
