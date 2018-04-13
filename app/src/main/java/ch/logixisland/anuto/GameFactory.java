package ch.logixisland.anuto;

import android.content.Context;

import ch.logixisland.anuto.business.game.GameConfigurationLoader;
import ch.logixisland.anuto.business.game.GameSpeed;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.HighScores;
import ch.logixisland.anuto.business.game.MapRepository;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.business.setting.SettingsManager;
import ch.logixisland.anuto.business.tower.TowerAging;
import ch.logixisland.anuto.business.tower.TowerControl;
import ch.logixisland.anuto.business.tower.TowerInserter;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.business.wave.WaveManager;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.entity.EntityStore;
import ch.logixisland.anuto.engine.logic.loop.FrameRateLogger;
import ch.logixisland.anuto.engine.logic.loop.GameLoop;
import ch.logixisland.anuto.engine.logic.loop.MessageQueue;
import ch.logixisland.anuto.engine.logic.persistence.GamePersister;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.sound.SoundFactory;
import ch.logixisland.anuto.engine.sound.SoundManager;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.entity.enemy.Blob;
import ch.logixisland.anuto.entity.enemy.Flyer;
import ch.logixisland.anuto.entity.enemy.Healer;
import ch.logixisland.anuto.entity.enemy.Soldier;
import ch.logixisland.anuto.entity.enemy.Sprinter;
import ch.logixisland.anuto.entity.plateau.BasicPlateau;
import ch.logixisland.anuto.entity.tower.BouncingLaser;
import ch.logixisland.anuto.entity.tower.Canon;
import ch.logixisland.anuto.entity.tower.DualCanon;
import ch.logixisland.anuto.entity.tower.GlueGun;
import ch.logixisland.anuto.entity.tower.GlueTower;
import ch.logixisland.anuto.entity.tower.MachineGun;
import ch.logixisland.anuto.entity.tower.MineLayer;
import ch.logixisland.anuto.entity.tower.Mortar;
import ch.logixisland.anuto.entity.tower.RocketLauncher;
import ch.logixisland.anuto.entity.tower.SimpleLaser;
import ch.logixisland.anuto.entity.tower.StraightLaser;
import ch.logixisland.anuto.entity.tower.Teleporter;

public class GameFactory {

    // Engine
    private ThemeManager mThemeManager;
    private SoundManager mSoundManager;
    private SpriteFactory mSpriteFactory;
    private SoundFactory mSoundFactory;
    private Viewport mViewport;
    private FrameRateLogger mFrameRateLogger;
    private EntityStore mEntityStore;
    private MessageQueue mMessageQueue;
    private Renderer mRenderer;
    private GameEngine mGameEngine;
    private GameLoop mGameLoop;
    private GamePersister mGamePersister;
    private EntityRegistry mEntityRegistry;

    // Business
    private ScoreBoard mScoreBoard;
    private HighScores mHighScores;
    private TowerSelector mTowerSelector;
    private TowerControl mTowerControl;
    private TowerAging mTowerAging;
    private TowerInserter mTowerInserter;
    private MapRepository mMapRepository;
    private GameConfigurationLoader mGameConfigurationLoader;
    private WaveManager mWaveManager;
    private GameSpeed mSpeedManager;
    private GameState mGameState;
    private SettingsManager mSettingsManager;

    public GameFactory(Context context) {
        initializeEngine(context);
        registerEntities();
        initializeBusiness(context);
        registerPersisters();

        mGameState.restart();
    }

    private void initializeBusiness(Context context) {
        mMapRepository = new MapRepository();
        mScoreBoard = new ScoreBoard(mGameEngine);
        mGameState = new GameState(mGameEngine, mThemeManager, mScoreBoard);
        mGameConfigurationLoader = new GameConfigurationLoader(context, mGameEngine, mScoreBoard, mGameState, mViewport, mEntityRegistry, mMapRepository);
        mTowerAging = new TowerAging(mGameEngine);
        mSpeedManager = new GameSpeed(mGameEngine);
        mWaveManager = new WaveManager(mGameEngine, mScoreBoard, mGameState, mEntityRegistry, mTowerAging);
        mHighScores = new HighScores(context, mGameState, mScoreBoard, mGameConfigurationLoader);
        mTowerSelector = new TowerSelector(mGameEngine, mGameState, mScoreBoard);
        mTowerControl = new TowerControl(mGameEngine, mScoreBoard, mTowerSelector, mEntityRegistry);
        mTowerInserter = new TowerInserter(mGameEngine, mGameState, mEntityRegistry, mTowerSelector, mTowerAging, mScoreBoard);
        mSettingsManager = new SettingsManager(context, mThemeManager, mSoundManager);
    }

    private void initializeEngine(Context context) {
        mThemeManager = new ThemeManager(context);
        mSoundManager = new SoundManager(context);
        mSpriteFactory = new SpriteFactory(context, mThemeManager);
        mSoundFactory = new SoundFactory(context, mSoundManager);
        mViewport = new Viewport();
        mFrameRateLogger = new FrameRateLogger();
        mEntityStore = new EntityStore();
        mMessageQueue = new MessageQueue();
        mRenderer = new Renderer(mViewport, mThemeManager, mFrameRateLogger);
        mGameLoop = new GameLoop(mRenderer, mFrameRateLogger);
        mGameEngine = new GameEngine(mSpriteFactory, mThemeManager, mSoundFactory, mEntityStore, mMessageQueue, mRenderer, mGameLoop);
        mEntityRegistry = new EntityRegistry(mGameEngine);
        mGamePersister = new GamePersister();
    }

    private void registerEntities() {
        mEntityRegistry.registerEntity(new BasicPlateau.Factory());

        mEntityRegistry.registerEntity(new Blob.Factory());
        mEntityRegistry.registerEntity(new Flyer.Factory());
        mEntityRegistry.registerEntity(new Healer.Factory());
        mEntityRegistry.registerEntity(new Soldier.Factory());
        mEntityRegistry.registerEntity(new Sprinter.Factory());

        mEntityRegistry.registerEntity(new Canon.Factory());
        mEntityRegistry.registerEntity(new DualCanon.Factory());
        mEntityRegistry.registerEntity(new MachineGun.Factory());
        mEntityRegistry.registerEntity(new SimpleLaser.Factory());
        mEntityRegistry.registerEntity(new BouncingLaser.Factory());
        mEntityRegistry.registerEntity(new StraightLaser.Factory());
        mEntityRegistry.registerEntity(new Mortar.Factory());
        mEntityRegistry.registerEntity(new MineLayer.Factory());
        mEntityRegistry.registerEntity(new RocketLauncher.Factory());
        mEntityRegistry.registerEntity(new GlueTower.Factory());
        mEntityRegistry.registerEntity(new GlueGun.Factory());
        mEntityRegistry.registerEntity(new Teleporter.Factory());

    }

    private void registerPersisters() {
        mGamePersister.registerPersister(mEntityRegistry);
        mGamePersister.registerPersister(mMessageQueue);
        mGamePersister.registerPersister(mGameConfigurationLoader);
        mGamePersister.registerPersister(mScoreBoard);
        mGamePersister.registerPersister(mWaveManager);

        mGamePersister.registerPersister(new BasicPlateau.Persister(mGameEngine, mEntityRegistry));

        mGamePersister.registerPersister(new Blob.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new Flyer.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new Healer.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new Soldier.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new Sprinter.Persister(mGameEngine, mEntityRegistry));

        mGamePersister.registerPersister(new Canon.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new DualCanon.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new MachineGun.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new SimpleLaser.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new BouncingLaser.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new StraightLaser.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new Mortar.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new MineLayer.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new RocketLauncher.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new GlueTower.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new GlueGun.Persister(mGameEngine, mEntityRegistry));
        mGamePersister.registerPersister(new Teleporter.Persister(mGameEngine, mEntityRegistry));
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

    public GameConfigurationLoader getGameConfigurationLoader() {
        return mGameConfigurationLoader;
    }

    public WaveManager getWaveManager() {
        return mWaveManager;
    }

    public GameSpeed getSpeedManager() {
        return mSpeedManager;
    }

    public GameState getGameState() {
        return mGameState;
    }

    public SettingsManager getSettingsManager() {
        return mSettingsManager;
    }

    public MapRepository getMapRepository() {
        return mMapRepository;
    }

    public HighScores getHighScores() {
        return mHighScores;
    }

}
