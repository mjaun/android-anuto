package ch.logixisland.anuto.business.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.R;

public class LevelRepository {

    private final List<LevelInfo> mLevels;

    public LevelRepository() {
        mLevels = new ArrayList<>();
        mLevels.add(new LevelInfo("original", R.string.level_original_name, R.raw.level_original));
        mLevels.add(new LevelInfo("waiting_line", R.string.level_waiting_line_name, R.raw.level_waiting_line));
        mLevels.add(new LevelInfo("turn_round", R.string.level_turn_round_name, R.raw.level_turn_round));
        mLevels.add(new LevelInfo("hurry", R.string.level_hurry_name, R.raw.level_hurry));
        mLevels.add(new LevelInfo("civyshk_yard", R.string.level_civyshk_yard_name, R.raw.level_civyshk_yard));
        mLevels.add(new LevelInfo("civyshk_2y", R.string.level_civyshk_2y_name, R.raw.level_civyshk_2y));
        mLevels.add(new LevelInfo("civyshk_line5", R.string.level_civyshk_line5_name, R.raw.level_civyshk_line5));
        mLevels.add(new LevelInfo("civyshk_labyrinth", R.string.level_civyshk_labyrinth_name, R.raw.level_civyshk_labyrinth));
    }

    public List<LevelInfo> getLevels() {
        return Collections.unmodifiableList(mLevels);
    }

}
