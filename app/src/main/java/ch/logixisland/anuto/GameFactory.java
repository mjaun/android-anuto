package ch.logixisland.anuto;

import android.content.Context;

import ch.logixisland.anuto.business.control.TowerControl;
import ch.logixisland.anuto.business.control.TowerInserter;
import ch.logixisland.anuto.business.control.TowerSelector;
import ch.logixisland.anuto.business.level.GameSpeedManager;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.level.LevelRepository;
import ch.logixisland.anuto.business.level.TowerAging;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.BackButtonControl;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.manager.SettingsManager;
import ch.logixisland.anuto.business.score.HighScores;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.EntityDependencies;
import ch.logixisland.anuto.engine.logic.EntityStore;
import ch.logixisland.anuto.engine.logic.FrameRateLogger;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.GameLoop;
import ch.logixisland.anuto.engine.logic.MessageQueue;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.sound.SoundFactory;
import ch.logixisland.anuto.engine.sound.SoundManager;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.entity.plateau.PlateauFactory;
import ch.logixisland.anuto.entity.tower.TowerFactory;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;

public class GameFactory {

    // Engine
    private final ThemeManager mThemeManager;
    private final SoundManager mSoundManager;
    private final SpriteFactory mSpriteFactory;
    private final SoundFactory mSoundFactory;
    private final Viewport mViewport;
    private final FrameRateLogger mFrameRateLogger;
    private final EntityStore mEntityStore;
    private final MessageQueue mMessageQueue;
    private final Renderer mRenderer;
    private final GameEngine mGameEngine;
    private final GameLoop mGameLoop;

    // Entity
    private final PlateauFactory mPlateauFactory;
    private final TowerFactory mTowerFactory;
    private final EnemyFactory mEnemyFactory;

    // Business
    private final ScoreBoard mScoreBoard;
    private final HighScores mHighScores;
    private final TowerSelector mTowerSelector;
    private final TowerControl mTowerControl;
    private final TowerAging mTowerAging;
    private final TowerInserter mTowerInserter;
    private final LevelRepository mLevelRepository;
    private final LevelLoader mLevelLoader;
    private final WaveManager mWaveManager;
    private final GameSpeedManager mSpeedManager;
    private final GameManager mGameManager;
    private final SettingsManager mSettingsManager;
    private final BackButtonControl mBackButtonControl;

    public GameFactory(Context context) {
        // Engine
        mThemeManager = new ThemeManager(context);
        mSoundManager = new SoundManager(context);
        mSpriteFactory = new SpriteFactory(context, mThemeManager);
        mSoundFactory = new SoundFactory(context, mSoundManager);
        mViewport = new Viewport();
        mFrameRateLogger = new FrameRateLogger();
        mEntityStore = new EntityStore();
        mMessageQueue = new MessageQueue();
        mRenderer = new Renderer(mViewport, mThemeManager, mFrameRateLogger);
        mGameLoop = new GameLoop(mEntityStore, mMessageQueue, mRenderer, mFrameRateLogger);
        mGameEngine = new GameEngine(mSpriteFactory, mThemeManager, mSoundFactory, mEntityStore, mMessageQueue, mRenderer, mGameLoop);

        // Entity
        EntityDependencies dependencyProvider = createEntityDependencyProvider();
        mPlateauFactory = new PlateauFactory(dependencyProvider);
        mTowerFactory = new TowerFactory(dependencyProvider);
        mEnemyFactory = new EnemyFactory(dependencyProvider);

        // Business
        mScoreBoard = new ScoreBoard();
        mLevelRepository = new LevelRepository();
        mSpeedManager = new GameSpeedManager(mGameEngine);
        mGameManager = new GameManager(mGameEngine, mThemeManager, mScoreBoard);
        mLevelLoader = new LevelLoader(context, mGameEngine, mScoreBoard, mGameManager, mViewport, mPlateauFactory, mTowerFactory, mEnemyFactory);
        mLevelLoader.loadLevel(mLevelRepository.getLevels().get(0));
        mWaveManager = new WaveManager(mGameEngine, mScoreBoard, mGameManager, mLevelLoader, mEnemyFactory);
        mTowerAging = new TowerAging(mGameEngine, mWaveManager, mLevelLoader);
        mHighScores = new HighScores(context, mGameManager, mScoreBoard, mLevelLoader);
        mTowerSelector = new TowerSelector(mGameEngine, mGameManager, mScoreBoard);
        mTowerControl = new TowerControl(mGameEngine, mScoreBoard, mTowerSelector, mTowerFactory);
        mTowerInserter = new TowerInserter(mGameEngine, mGameManager, mTowerFactory, mTowerSelector, mTowerAging, mScoreBoard);
        mSettingsManager = new SettingsManager(context, mThemeManager, mSoundManager);
        mBackButtonControl = new BackButtonControl(mSettingsManager);

        mGameManager.restart();
    }

    public ThemeManager getThemeManager() {
        return mThemeManager;
    }

    public Viewport getViewport() {
        return mViewport;
    }

    public Renderer getRenderer() {
        return mRenderer;
    }

    public GameEngine getGameEngine() {
        return mGameEngine;
    }

    public TowerFactory getTowerFactory() {
        return mTowerFactory;
    }

    public ScoreBoard getScoreBoard() {
        return mScoreBoard;
    }

    public TowerSelector getTowerSelector() {
        return mTowerSelector;
    }

    public TowerControl getTowerControl() {
        return mTowerControl;
    }

    public TowerInserter getTowerInserter() {
        return mTowerInserter;
    }

    public LevelLoader getLevelLoader() {
        return mLevelLoader;
    }

    public WaveManager getWaveManager() {
        return mWaveManager;
    }

    public GameSpeedManager getSpeedManager() {
        return mSpeedManager;
    }

    public GameManager getGameManager() {
        return mGameManager;
    }

    public SettingsManager getSettingsManager() {
        return mSettingsManager;
    }

    public BackButtonControl getBackButtonControl() {
        return mBackButtonControl;
    }

    public LevelRepository getLevelRepository() {
        return mLevelRepository;
    }

    public HighScores getHighScores() {
        return mHighScores;
    }

    private EntityDependencies createEntityDependencyProvider() {
        return new EntityDependencies() {
            @Override
            public GameEngine getGameEngine() {
                return mGameEngine;
            }

            @Override
            public GameSettings getGameSettings() {
                return mLevelLoader.getGameSettings();
            }

            @Override
            public LevelDescriptor getLevelDescriptor() {
                return mLevelLoader.getLevelDescriptor();
            }
        };
    }
}
