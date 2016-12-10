package ch.logixisland.anuto.game.business.level;

import java.util.Iterator;

import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.tower.Tower;

public class TowerAging {

    private final GameEngine mGameEngine;
    private final LevelLoader mLevelLoader;

    public TowerAging(GameEngine gameEngine, LevelLoader levelLoader) {
        mGameEngine = gameEngine;
        mLevelLoader = levelLoader;
    }

    public void ageTowers() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    ageTowers();
                }
            });
            return;
        }

        Iterator<Tower> it = mGameEngine.get(Types.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower tower = it.next();

            if (tower.isEnabled()) {
                tower.devalue(mLevelLoader.getSettings().getAgeModifier());
            }
        }
    }

}
