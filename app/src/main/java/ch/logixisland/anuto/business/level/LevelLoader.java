package ch.logixisland.anuto.business.level;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.util.data.LevelDescriptor;
import ch.logixisland.anuto.util.data.PlateauDescriptor;
import ch.logixisland.anuto.util.data.Settings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.engine.render.Viewport;

public class LevelLoader {

    private final GameEngine mGameEngine;
    private final Viewport mViewport;
    private final ScoreBoard mScoreBoard;

    private LevelDescriptor mLevelDescriptor;

    public LevelLoader(GameEngine gameEngine, Viewport viewport, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mViewport = viewport;
        mScoreBoard = scoreBoard;
    }

    public LevelDescriptor getLevel() {
        return mLevelDescriptor;
    }

    public void setLevel(LevelDescriptor levelDescriptor) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final LevelDescriptor finalDescriptor = levelDescriptor;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    setLevel(finalDescriptor);
                }
            });
            return;
        }

        mLevelDescriptor = levelDescriptor;
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

        for (PlateauDescriptor d : mLevelDescriptor.getPlateaus()) {
            Plateau p = d.createInstance();
            p.setPosition(d.getX(), d.getY());
            mGameEngine.add(p);
        }

        Settings settings = mLevelDescriptor.getSettings();
        mViewport.setGameSize(settings.getWidth(), settings.getHeight());
        mScoreBoard.reset(settings.getLives(), settings.getCredits());
    }

}
