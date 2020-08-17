package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

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

        saveGameState(mSaveGameRepository.getAutoSaveStateFile().getAbsolutePath());
    }

    public File saveGame() {
        if (mGameEngine.isThreadRunning()) {
            throw new RuntimeException("This method cannot be used when the game thread is running!");
        }

        SaveGameInfo saveGameInfo = mSaveGameRepository.createSaveGame(
                mRenderer.getScreenshot(),
                mScoreBoard.getScore(),
                mWaveManager.getWaveNumber(),
                mScoreBoard.getLives()
        );

        saveGameState(saveGameInfo.getGameStatePath());
        return saveGameInfo.getFolder();
    }

    void saveGameState(String fileName) {
        Log.i(TAG, "Saving game...");
        KeyValueStore gameState = new KeyValueStore();
        mGamePersister.writeState(gameState);
        gameState.putInt("appVersion", BuildConfig.VERSION_CODE);
        gameState.putString("mapId", mGameLoader.getCurrentMapId());

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName, false);
            gameState.toStream(outputStream);
            outputStream.close();
            Log.i(TAG, "Game saved.");
        } catch (Exception e) {
            mContext.deleteFile(fileName);
            throw new RuntimeException("Could not save game!", e);
        }
    }
}
