package ch.logixisland.anuto.business.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.R;

public class MapRepository {

    private final List<MapInfo> mMaps;

    public MapRepository() {
        mMaps = new ArrayList<>();
        mMaps.add(new MapInfo("original", R.string.map_original_name, R.raw.map_original));
        mMaps.add(new MapInfo("waiting_line", R.string.map_waiting_line_name, R.raw.map_waiting_line));
        mMaps.add(new MapInfo("waiting_line", R.string.map_waiting_line_name, R.raw.map_waiting_line));
        mMaps.add(new MapInfo("turn_round", R.string.map_turn_round_name, R.raw.map_turn_round));
        mMaps.add(new MapInfo("hurry", R.string.map_hurry_name, R.raw.map_hurry));
        mMaps.add(new MapInfo("civyshk_yard", R.string.map_civyshk_yard_name, R.raw.map_civyshk_yard));
        mMaps.add(new MapInfo("civyshk_2y", R.string.map_civyshk_2y_name, R.raw.map_civyshk_2y));
        mMaps.add(new MapInfo("civyshk_line5", R.string.map_civyshk_line5_name, R.raw.map_civyshk_line5));
        mMaps.add(new MapInfo("civyshk_labyrinth", R.string.map_civyshk_labyrinth_name, R.raw.map_civyshk_labyrinth));
    }

    public List<MapInfo> getMaps() {
        return Collections.unmodifiableList(mMaps);
    }

}
