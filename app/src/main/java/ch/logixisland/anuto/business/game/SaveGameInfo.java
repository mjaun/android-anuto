package ch.logixisland.anuto.business.game;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Date;

public final class SaveGameInfo {

    private final File mFolder;
    private final Date mDate;
    private final int mScore;
    private final int mWave;
    private final int mLives;
    private final Bitmap mScreenshot;

    public SaveGameInfo(File folder, Date date, int score, int wave, int lives, Bitmap screenshot) {
        mFolder = folder;
        mDate = date;
        mScore = score;
        mWave = wave;
        mLives = lives;
        mScreenshot = screenshot;
    }

    public File getFolder() {
        return mFolder;
    }

    public Date getDate() {
        return mDate;
    }

    public int getScore() {
        return mScore;
    }

    public int getWave() {
        return mWave;
    }

    public int getLives() {
        return mLives;
    }

    public Bitmap getScreenshot() {
        return mScreenshot;
    }

}
