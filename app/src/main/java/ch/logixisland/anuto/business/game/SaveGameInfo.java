package ch.logixisland.anuto.business.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;

import ch.logixisland.anuto.util.container.KeyValueStore;

public final class SaveGameInfo {
    final private File mFolder;
    final private KeyValueStore mSavegameInfoStore;
    final private Bitmap mCachedScreenshot;

    public static SaveGameInfo createSGI(File folder) {
        try {
            KeyValueStore savegameInfoStore = KeyValueStore.fromStream(new FileInputStream(new File(folder, SaveGameRepository.GAME_INFO_FILE)));
            Bitmap cachedScreenshot = BitmapFactory.decodeFile(new File(folder, SaveGameRepository.SCREENSHOT_FILE).getAbsolutePath());
            return new SaveGameInfo(folder, savegameInfoStore, cachedScreenshot);
        } catch (Exception e) {
            return null;
        }
    }

    private SaveGameInfo(File folder, KeyValueStore savegameInfoStore, Bitmap cachedScreenshot) {
        mFolder = folder;
        mSavegameInfoStore = savegameInfoStore;
        mCachedScreenshot = cachedScreenshot;
    }

    public File getFolder() {
        return mFolder;
    }

    public String getGameStatePath() {
        return new File(mFolder, SaveGameRepository.GAME_STATE_FILE).getAbsolutePath();
    }

    public Bitmap getCachedScreenshot() {
        return mCachedScreenshot;
    }

    public String getDatetime() {
        return mSavegameInfoStore.getString("dateTime");
    }

    public int getScore() {
        return mSavegameInfoStore.getInt("score");
    }

    public int getWave() {
        return mSavegameInfoStore.getInt("waveNumber");
    }

    public int getRemainingEnemiesCount() {
        return mSavegameInfoStore.getInt("remainingEnemiesCount");
    }

    public int getCredits() {
        return mSavegameInfoStore.getInt("credits");
    }

    public int getLives() {
        return mSavegameInfoStore.getInt("lives");
    }
}
