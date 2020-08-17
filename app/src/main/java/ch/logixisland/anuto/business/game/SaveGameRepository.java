package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SaveGameRepository {

    private static final String TAG = GameLoader.class.getSimpleName();

    private final List<SaveGameInfo> mSavegameInfos;
    private final Context mContext;

    public SaveGameRepository(Context context) {
        mContext = context;
        mSavegameInfos = new ArrayList<>();
    }

    public void refresh(GameLoader gameLoader) {
        mSavegameInfos.clear();

        File rootdir = new File(mContext.getFilesDir() + File.separator
                + "savegame" + File.separator
                + gameLoader.getCurrentMapId());

        File[] files = rootdir.listFiles();

        if ((files == null) || (files.length == 0)) {
            Log.d(TAG, "No Files Found");
        } else {
            Log.d(TAG, "Size: " + files.length);
            final List<File> lfiles = new ArrayList<>(Arrays.asList(files));
            Collections.sort(lfiles, Collections.reverseOrder());

            for (File one : lfiles) {
                Log.d(TAG, "FileName:" + one.getName());
                SaveGameInfo sgi = SaveGameInfo.createSGI(one);
                if (sgi != null)
                    mSavegameInfos.add(sgi);
            }
        }
    }

    public void removeSGIAt(int position) {
        if (mSavegameInfos.size() > position) {
            deleteSavegame(mSavegameInfos.get(position).getFolder());
            mSavegameInfos.remove(position);
        }
    }

    public List<SaveGameInfo> getSavegameInfos() {
        return Collections.unmodifiableList(mSavegameInfos);
    }

    public void deleteSavegame(File rootdir) {
        File[] files = rootdir.listFiles();

        if ((files == null) || (files.length == 0)) {
            Log.d(TAG, "No Files Found");
        } else if (files.length != 3) {
            Log.d(TAG, "Incorrect File Count");
        } else {
            Log.d(TAG, "Size: " + files.length);

            final List<String> ldeleteThis = Arrays.asList(GameLoader.SAVED_GAME_FILE, GameLoader.SAVED_SCREENSHOT_FILE, GameLoader.SAVED_GAMEINFO_FILE);
            final List<File> lfiles = new ArrayList<>(Arrays.asList(files));
            List<File> result = new ArrayList<>();
            for (File one : lfiles) {
                Log.d(TAG, "FileName:" + one.getName());
                if (ldeleteThis.contains(one.getName()))
                    result.add(one);
            }
            if (result.size() != lfiles.size())
                return;
            for (File one : result) {
                one.delete();
            }
            rootdir.delete();
        }
    }

    public boolean hasSavegames(GameLoader gameLoader) {
        File rootdir = new File(mContext.getFilesDir() + File.separator
                + "savegame" + File.separator
                + gameLoader.getCurrentMapId());

        File[] files = rootdir.listFiles();

        return (files != null) && (files.length > 0);

    }
}
