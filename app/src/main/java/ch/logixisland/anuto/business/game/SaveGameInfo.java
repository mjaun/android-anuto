package ch.logixisland.anuto.business.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;

import ch.logixisland.anuto.util.container.KeyValueStore;

public final class SaveGameInfo {
    final private File mFolder;
    final private KeyValueStore mSavegameState;
    final private KeyValueStore mSavegameInfoStore;
    final private Bitmap mCachedScreenshot;

    public static SaveGameInfo createSGI(GameLoader gameLoader, File folder) {
        try {
            KeyValueStore savegameState = gameLoader.readSaveGame(new File(folder, GameLoader.SAVED_GAME_FILE).getAbsolutePath());
            KeyValueStore savegameInfoStore = KeyValueStore.fromStream(new FileInputStream(new File(folder, GameLoader.SAVED_GAMEINFO_FILE)));
            Bitmap cachedScreenshot = BitmapFactory.decodeFile(new File(folder, GameLoader.SAVED_SCREENSHOT_FILE).getAbsolutePath());
            return new SaveGameInfo(folder, savegameState, savegameInfoStore, cachedScreenshot);
        } catch (Exception e) {
            //throw new RuntimeException("Could not read save game!", e);
            return null;
        }
    }

    private SaveGameInfo(File folder, KeyValueStore savegameState, KeyValueStore savegameInfoStore, Bitmap cachedScreenshot) {
        mFolder = folder;
        mSavegameState = savegameState;
        mSavegameInfoStore = savegameInfoStore;
        mCachedScreenshot = cachedScreenshot;
    }

    public File getFolder() {
        return mFolder;
    }

    public KeyValueStore getSavegameState() {
        return mSavegameState;
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
