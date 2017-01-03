package ch.logixisland.anuto;

import android.content.Context;

import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.level.TowerAging;
import ch.logixisland.anuto.business.control.TowerControl;
import ch.logixisland.anuto.business.control.TowerInserter;
import ch.logixisland.anuto.business.control.TowerSelector;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.engine.render.shape.ShapeFactory;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.entity.plateau.PlateauFactory;
import ch.logixisland.anuto.entity.tower.TowerFactory;

public class GameFactory {

    private final ThemeManager mThemeManager;
    private final SpriteFactory mSpriteFactory;
    private final ShapeFactory mShapeFactory;
    private final Viewport mViewport;
    private final Renderer mRenderer;

    private final GameEngine mGameEngine;
    private final PlateauFactory mPlateauFactory;
    private final TowerFactory mTowerFactory;
    private final EnemyFactory mEnemyFactory;

    private final ScoreBoard mScoreBoard;
    private final TowerSelector mTowerSelector;
    private final TowerControl mTowerControl;
    private final TowerAging mTowerAging;
    private final TowerInserter mTowerInserter;
    private final LevelLoader mLevelLoader;
    private final WaveManager mWaveManager;
    private final GameManager mGameManager;

    public GameFactory(Context context) {
        mThemeManager = new ThemeManager();
        mSpriteFactory = new SpriteFactory(context.getResources(), mThemeManager);
        mShapeFactory = new ShapeFactory(mThemeManager);
        mViewport = new Viewport();
        mRenderer = new Renderer(mViewport, mThemeManager);

        mGameEngine = new GameEngine(mRenderer);
        mPlateauFactory = new PlateauFactory();
        mTowerFactory = new TowerFactory();
        mEnemyFactory = new EnemyFactory();

        mScoreBoard = new ScoreBoard();
        mTowerSelector = new TowerSelector(mGameEngine);
        mTowerControl = new TowerControl(mGameEngine, mScoreBoard, mTowerSelector);
        mLevelLoader = new LevelLoader(mGameEngine, mViewport, mScoreBoard, mPlateauFactory);
        mWaveManager = new WaveManager(mGameEngine, mScoreBoard, mLevelLoader, mEnemyFactory);
        mTowerAging = new TowerAging(mGameEngine, mWaveManager, mLevelLoader);
        mTowerInserter = new TowerInserter(mGameEngine, mScoreBoard, mTowerSelector, mTowerAging);
        mGameManager = new GameManager(mGameEngine, mScoreBoard, mLevelLoader, mWaveManager);
    }

    public SpriteFactory getSpriteFactory() {
        return mSpriteFactory;
    }

    public ThemeManager getThemeManager() {
        return mThemeManager;
    }

    public GameEngine getGameEngine() {
        return mGameEngine;
    }

    public GameManager getGameManager() {
        return mGameManager;
    }

    public Viewport getViewport() {
        return mViewport;
    }

    public Renderer getRenderer() {
        return mRenderer;
    }

    public ShapeFactory getShapeFactory() {
        return mShapeFactory;
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

    public TowerFactory getTowerFactory() {
        return mTowerFactory;
    }
}
