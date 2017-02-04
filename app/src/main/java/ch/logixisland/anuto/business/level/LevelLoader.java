package ch.logixisland.anuto.business.level;

import android.content.Context;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.entity.enemy.EnemyFactory;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.plateau.PlateauFactory;
import ch.logixisland.anuto.entity.tower.TowerFactory;
import ch.logixisland.anuto.util.data.EnemySettings;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;
import ch.logixisland.anuto.util.data.PlateauDescriptor;
import ch.logixisland.anuto.util.data.TowerSettings;

public class LevelLoader {

    private final Context mContext;
    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;
    private final PlateauFactory mPlateauFactory;
    private final TowerFactory mTowerFactory;
    private final EnemyFactory mEnemyFactory;

    private GameSettings mGameSettings;
    private TowerSettings mTowerSettings;
    private EnemySettings mEnemySettings;
    private LevelDescriptor mLevelDescriptor;

    public LevelLoader(Context context, GameEngine gameEngine, Viewport viewport,
                       ScoreBoard scoreBoard, PlateauFactory plateauFactory,
                       TowerFactory towerFactory, EnemyFactory enemyFactory) {
        mContext = context;
        mGameEngine = gameEngine;
        mViewport = viewport;
        mScoreBoard = scoreBoard;
        mPlateauFactory = plateauFactory;
        mTowerFactory = towerFactory;
        mEnemyFactory = enemyFactory;

        try {
            Persister serializer = new Persister();
            InputStream stream;

            stream = mContext.getResources().openRawResource(R.raw.game_settings);
            mGameSettings = serializer.read(GameSettings.class, stream);

            stream = mContext.getResources().openRawResource(R.raw.tower_settings);
            mTowerSettings = serializer.read(TowerSettings.class, stream);

            stream = mContext.getResources().openRawResource(R.raw.enemy_settings);
            mEnemySettings = serializer.read(EnemySettings.class, stream);
        } catch (Exception e) {
            throw new RuntimeException("Could not load settings!", e);
        }

        mTowerFactory.setTowerSettings(mTowerSettings);
        mEnemyFactory.setEnemySettings(mEnemySettings);
        loadLevel(R.raw.level_1);
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public TowerSettings getTowerSettings() {
        return mTowerSettings;
    }

    public EnemySettings getEnemySettings() {
        return mEnemySettings;
    }

    public LevelDescriptor getLevelDescriptor() {
        return mLevelDescriptor;
    }

    public void loadLevel(final int levelId) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    loadLevel(levelId);
                }
            });
        }

        try {
            Persister serializer = new Persister();
            InputStream stream;

            stream = mContext.getResources().openRawResource(levelId);
            mLevelDescriptor = serializer.read(LevelDescriptor.class, stream);
        } catch (Exception e) {
            throw new RuntimeException("Could not load level!", e);
        }
    }

    public void reset() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            });
            return;
        }

        mGameEngine.clear();

        for (PlateauDescriptor descriptor : mLevelDescriptor.getPlateaus()) {
            Plateau p = mPlateauFactory.createPlateau(descriptor.getName());
            p.setPosition(descriptor.getPosition());
            mGameEngine.add(p);
        }

        mViewport.setGameSize(mLevelDescriptor.getWidth(), mLevelDescriptor.getHeight());
        mScoreBoard.reset(mGameSettings.getLives(), mGameSettings.getCredits());
    }

}
