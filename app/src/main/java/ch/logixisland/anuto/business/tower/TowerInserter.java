package ch.logixisland.anuto.business.tower;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.container.KeyValueStore;
import ch.logixisland.anuto.util.math.Vector2;

public class TowerInserter implements Persister {

    public interface Listener {
        void towerInserted();
    }

    private final GameEngine mGameEngine;
    private final GameState mGameState;
    private final EntityRegistry mEntityRegistry;
    private final TowerSelector mTowerSelector;
    private final TowerAging mTowerAging;
    private final ScoreBoard mScoreBoard;

    private final TowerDefaultValue mTowerDefaultValue;

    private Tower mInsertedTower;
    private Plateau mCurrentPlateau;
    private KeyValueStore mSlotConfig;
    private Collection<Listener> mListeners = new CopyOnWriteArrayList<>();

    public TowerInserter(GameEngine gameEngine, GameState gameState, EntityRegistry entityRegistry,
                         TowerSelector towerSelector, TowerAging towerAging, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mGameState = gameState;
        mEntityRegistry = entityRegistry;
        mTowerSelector = towerSelector;
        mTowerAging = towerAging;
        mScoreBoard = scoreBoard;

        mTowerDefaultValue = new TowerDefaultValue(entityRegistry);
    }

    public void insertTower(final String towerName) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    insertTower(towerName);
                }
            });
            return;
        }

        if (mInsertedTower == null && !mGameState.isGameOver() &&
                mScoreBoard.getCredits() >= mTowerDefaultValue.getDefaultValue(towerName)) {
            showTowerLevels();
            mInsertedTower = (Tower) mEntityRegistry.createEntity(towerName);
        }
    }

    public Tower createPreviewTower(int slot) {
        String towerName = mSlotConfig.getString(Integer.toString(slot));
        return (Tower) mEntityRegistry.createEntity(towerName);
    }

    public void setPosition(final Vector2 position) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    setPosition(position);
                }
            });
            return;
        }

        if (mInsertedTower != null) {
            Plateau closestPlateau = mGameEngine.getEntitiesByType(Types.PLATEAU)
                    .cast(Plateau.class)
                    .filter(Plateau.unoccupied())
                    .min(Entity.distanceTo(position));

            if (closestPlateau != null) {
                if (mCurrentPlateau == null) {
                    mGameEngine.add(mInsertedTower);
                    mTowerSelector.selectTower(mInsertedTower);
                }

                mCurrentPlateau = closestPlateau;
                mInsertedTower.setPosition(mCurrentPlateau.getPosition());
            } else {
                cancel();
            }
        }
    }

    public void buyTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    buyTower();
                }
            });
            return;
        }

        if (mInsertedTower != null && mCurrentPlateau != null) {
            mInsertedTower.setPlateau(mCurrentPlateau);
            mInsertedTower.setEnabled(true);

            mScoreBoard.takeCredits(mInsertedTower.getValue());
            mTowerAging.ageTower(mInsertedTower);

            mTowerSelector.selectTower(null);
            hideTowerLevels();

            mCurrentPlateau = null;
            mInsertedTower = null;

            for (Listener listener : mListeners) {
                listener.towerInserted();
            }
        }
    }

    public void cancel() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    cancel();
                }
            });
            return;
        }

        if (mInsertedTower != null) {
            mGameEngine.remove(mInsertedTower);

            hideTowerLevels();
            mCurrentPlateau = null;
            mInsertedTower = null;
        }
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void resetState(KeyValueStore gameConfig) {
        mSlotConfig = gameConfig.getStore("slots");
    }

    @Override
    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        resetState(gameConfig);
    }

    @Override
    public void writeState(KeyValueStore gameState) {

    }

    private void showTowerLevels() {
        Iterator<Tower> towers = mGameEngine.getEntitiesByType(Types.TOWER).cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            tower.showLevel();
        }
    }

    private void hideTowerLevels() {
        Iterator<Tower> towers = mGameEngine.getEntitiesByType(Types.TOWER).cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            tower.hideLevel();
        }
    }

}
