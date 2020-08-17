package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.business.wave.WaveManager;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.persistence.GamePersister;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class GameSaver {

    private static final String TAG = GameLoader.class.getSimpleName();

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final GameLoader mGameLoader;
    private final GamePersister mGamePersister;
    private final Renderer mRenderer;
    private final WaveManager mWaveManager;
    private final ScoreBoard mScoreBoard;
    private final SaveGameRepository mSaveGameRepository;

    public GameSaver(Context context, GameEngine gameEngine, GameLoader gameLoader, GamePersister gamePersister,
                     Renderer renderer, WaveManager waveManager, ScoreBoard scoreBoard, SaveGameRepository saveGameRepository) {
        mContext = context;
        mGameEngine = gameEngine;
        mGameLoader = gameLoader;
        mGamePersister = gamePersister;
        mRenderer = renderer;
        mWaveManager = waveManager;
        mScoreBoard = scoreBoard;
        mSaveGameRepository = saveGameRepository;
    }

    public void autoSaveGame() {
        if (mGameEngine.isThreadRunning() && mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    autoSaveGame();
                }
            });
            return;
        }

        saveGame(GameLoader.SAVED_GAME_FILE, false);
    }

    void saveGame(final String fileName, final boolean userSavegame) {
        Log.i(TAG, "Saving game...");
        KeyValueStore gameState = new KeyValueStore();
        mGamePersister.writeState(gameState);
        gameState.putInt("appVersion", BuildConfig.VERSION_CODE);
        gameState.putString("mapId", mGameLoader.getCurrentMapId());

        try {
            FileOutputStream outputStream = userSavegame ? (new FileOutputStream(fileName, false)) : mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            gameState.toStream(outputStream);
            outputStream.close();
            Log.i(TAG, "Game saved.");
        } catch (Exception e) {
            mContext.deleteFile(fileName);
            throw new RuntimeException("Could not save game!", e);
        }
    }

    public File makeNewSavegame() {
        /*if (mGameEngine.isThreadRunning() && mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    makeNewSavegame();
                }
            });
            return;
        }*/

        Date now = new Date();
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        File rootdir = new File(mContext.getFilesDir() + File.separator
                + "savegame" + File.separator
                + mGameLoader.getCurrentMapId() + File.separator
                + dtFormat.format(now));
        rootdir.mkdirs();

        Bitmap bitmap = mRenderer.getScreenshot();

        try {
            Log.i(TAG, "Saving screenshot...");

            FileOutputStream outputStream = new FileOutputStream(new File(rootdir, GameLoader.SAVED_SCREENSHOT_FILE), false);

            int destWidth = 600;
            int origWidth = bitmap.getWidth();

            if (destWidth < origWidth) {
                int origHeight = bitmap.getHeight();

                int destHeight = (int) (((float) origHeight) / (((float) origWidth) / destWidth));
                bitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 30, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i(TAG, "Screenshot saved.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save game!", e);
        }

        try {
            Log.i(TAG, "Creating savegame info...");
            saveGame(rootdir.getAbsolutePath() + File.separator + GameLoader.SAVED_GAME_FILE, true);

            KeyValueStore savegameInfo = new KeyValueStore();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            savegameInfo.putInt("appVersion", BuildConfig.VERSION_CODE);
            savegameInfo.putString("dateTime", dateFormat.format(now));
            savegameInfo.putInt("waveNumber", mWaveManager.getWaveNumber());
            savegameInfo.putInt("remainingEnemiesCount", mWaveManager.getRemainingEnemiesCount());
            savegameInfo.putInt("score", mScoreBoard.getScore());
            savegameInfo.putInt("credits", mScoreBoard.getCredits());
            savegameInfo.putInt("lives", mScoreBoard.getLives());

            FileOutputStream outputStream = new FileOutputStream(new File(rootdir, GameLoader.SAVED_GAMEINFO_FILE), false);
            savegameInfo.toStream(outputStream);
            outputStream.close();
            Log.i(TAG, "Savegame info saved.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save game!", e);
        }

        mSaveGameRepository.refresh(mGameLoader);

        return rootdir;
    }
}
