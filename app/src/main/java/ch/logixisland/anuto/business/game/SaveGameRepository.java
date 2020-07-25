package ch.logixisland.anuto.business.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaveGameRepository {

    private final List<SaveGameInfo> mSavegameInfos;
    private final GameLoader mGameLoader;

    public SaveGameRepository(GameLoader gameLoader) {
        mSavegameInfos = new ArrayList<>();
        mGameLoader = gameLoader;
    }

    public void addSGI(SaveGameInfo saveGameInfo) {
        mSavegameInfos.add(saveGameInfo);
    }

    public void removeSGIAt(int position) {
        if (mSavegameInfos.size() > position) {
            mGameLoader.deleteSavegame(mSavegameInfos.get(position).getFolder());
            mSavegameInfos.remove(position);
        }
    }

    public List<SaveGameInfo> getSavegameInfos() {
        return Collections.unmodifiableList(mSavegameInfos);
    }
}
