package ch.logixisland.anuto.business.tower;

import java.util.Iterator;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class TowerAging implements Persister {

    private final GameEngine mGameEngine;

    private float mAgeModifier;

    public TowerAging(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void ageTowers() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    ageTowers();
                }
            });
            return;
        }

        Iterator<Tower> towers = mGameEngine
                .getEntitiesByType(Types.TOWER)
                .cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            ageTower(tower);
        }
    }

    public void ageTower(final Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    ageTower(tower);
                }
            });
            return;
        }

        int value = tower.getValue();
        value = Math.round(value * mAgeModifier);
        tower.setValue(value);
    }

    @Override
    public void resetState(KeyValueStore gameConfig) {
        mAgeModifier = gameConfig.getFloat("ageModifier");
    }

    @Override
    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        resetState(gameConfig);
    }

    @Override
    public void writeState(KeyValueStore gameState) {

    }
}
