package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class SaveGameRepository {

    private static final String TAG = GameLoader.class.getSimpleName();

    private static final String AUTO_SAVE_STATE_FILE = "autosave.json";

    public static final String GAME_INFO_FILE = "info.json";
    public static final String GAME_STATE_FILE = "state.json";
    public static final String SCREENSHOT_FILE = "screen.png";

    private final List<SaveGameInfo> mSaveGameInfos;
    private final Context mContext;

    public SaveGameRepository(Context context) {
        mContext = context;
        mSaveGameInfos = new ArrayList<>();
    }

    public File getAutoSaveStateFile() {
        return new File(mContext.getFilesDir(), AUTO_SAVE_STATE_FILE);
    }

    public void refresh() {
        mSaveGameInfos.clear();

        File rootdir = new File(mContext.getFilesDir() + File.separator
                + "savegame" + File.separator);

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
                    mSaveGameInfos.add(sgi);
            }
        }
    }

    public SaveGameInfo createSaveGame(Bitmap screenshot, int score, int wave, int lives) {
        Date now = new Date();
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        File rootdir = new File(mContext.getFilesDir() + File.separator
                + "savegame" + File.separator
                + dtFormat.format(now));
        rootdir.mkdirs();

        try {
            Log.i(TAG, "Saving screenshot...");

            FileOutputStream outputStream = new FileOutputStream(new File(rootdir, SCREENSHOT_FILE), false);

            int destWidth = 600;
            int origWidth = screenshot.getWidth();

            if (destWidth < origWidth) {
                int origHeight = screenshot.getHeight();

                int destHeight = (int) (((float) origHeight) / (((float) origWidth) / destWidth));
                screenshot = Bitmap.createScaledBitmap(screenshot, destWidth, destHeight, false);
            }

            screenshot.compress(Bitmap.CompressFormat.PNG, 30, outputStream);
            outputStream.flush();
            outputStream.close();

            Log.i(TAG, "Screenshot saved.");
        } catch (IOException e) {
            throw new RuntimeException("Could not save screenshot!", e);
        }

        try {
            Log.i(TAG, "Creating savegame info...");
            KeyValueStore savegameInfo = new KeyValueStore();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            savegameInfo.putInt("appVersion", BuildConfig.VERSION_CODE);
            savegameInfo.putString("dateTime", dateFormat.format(now));
            savegameInfo.putInt("score", score);
            savegameInfo.putInt("waveNumber", wave);
            savegameInfo.putInt("lives", lives);

            FileOutputStream outputStream = new FileOutputStream(new File(rootdir, GAME_INFO_FILE), false);
            savegameInfo.toStream(outputStream);
            outputStream.close();
            Log.i(TAG, "Savegame info saved.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save game!", e);
        }

        SaveGameInfo sgi = SaveGameInfo.createSGI(rootdir);
        return sgi;
    }

    public void removeSGIAt(int position) {
        if (mSaveGameInfos.size() > position) {
            deleteSavegame(mSaveGameInfos.get(position).getFolder());
            mSaveGameInfos.remove(position);
        }
    }

    public List<SaveGameInfo> getSaveGameInfos() {
        return Collections.unmodifiableList(mSaveGameInfos);
    }

    public void deleteSavegame(File rootdir) {
        File[] files = rootdir.listFiles();

        if ((files == null) || (files.length == 0)) {
            Log.d(TAG, "No Files Found");
        } else if (files.length != 3) {
            Log.d(TAG, "Incorrect File Count");
        } else {
            Log.d(TAG, "Size: " + files.length);

            final List<String> ldeleteThis = Arrays.asList(GAME_STATE_FILE, GAME_INFO_FILE, SCREENSHOT_FILE);
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

    public boolean hasSavegames() {
        File rootdir = new File(mContext.getFilesDir() + File.separator
                + "savegame" + File.separator);

        File[] files = rootdir.listFiles();

        return (files != null) && (files.length > 0);

    }
}
