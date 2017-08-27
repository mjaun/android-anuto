package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.data.game.GameDescriptorRoot;
import ch.logixisland.anuto.data.game.TowerDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class TowerPersister implements Persister {

    private final GameEngine mGameEngine;
    private final EntityRegistry mEntityRegistry;
    private final String mEntityName;

    public TowerPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        mGameEngine = gameEngine;
        mEntityRegistry = entityRegistry;
        mEntityName = entityName;
    }

    @Override
    public void writeDescriptor(GameDescriptorRoot gameDescriptor) {
        StreamIterator<Tower> iterator = mGameEngine.getEntitiesByType(Types.TOWER)
                .filter(Entity.nameEquals(mEntityName))
                .cast(Tower.class);

        while (iterator.hasNext()) {
            Tower tower = iterator.next();
            TowerDescriptor towerDescriptor = writeTowerDescriptor(tower);
            gameDescriptor.addEntityDescriptor(towerDescriptor);
        }
    }

    @Override
    public void readDescriptor(GameDescriptorRoot gameDescriptor) {
        for (EntityDescriptor entityDescriptor : gameDescriptor.getEntityDescriptors()) {
            if (mEntityName.equals(entityDescriptor.getName())) {
                TowerDescriptor towerDescriptor = (TowerDescriptor) entityDescriptor;
                Tower tower = readTowerDescriptor(towerDescriptor);
                mGameEngine.add(tower);
            }
        }
    }

    protected TowerDescriptor createTowerDescriptor() {
        return new TowerDescriptor();
    }

    protected TowerDescriptor writeTowerDescriptor(Tower tower) {
        TowerDescriptor towerDescriptor = createTowerDescriptor();

        towerDescriptor.setName(tower.getEntityName());
        towerDescriptor.setPosition(tower.getPosition());
        towerDescriptor.setValue(tower.getValue());
        towerDescriptor.setLevel(tower.getLevel());
        towerDescriptor.setDamageInflicted(tower.getDamageInflicted());

        return towerDescriptor;
    }

    protected Tower readTowerDescriptor(TowerDescriptor towerDescriptor) {
        Tower tower = (Tower) mEntityRegistry.createEntity(towerDescriptor.getName());

        while (tower.getLevel() < towerDescriptor.getLevel()) {
            tower.enhance();
        }

        tower.setPlateau((Plateau) mGameEngine.getEntityById(towerDescriptor.getPlateauId()));
        tower.setValue(towerDescriptor.getValue());
        tower.setDamageInflicted(towerDescriptor.getDamageInflicted());
        tower.setEnabled(true);

        return tower;
    }

    protected GameEngine getGameEngine() {
        return mGameEngine;
    }

    protected EntityRegistry getEntityRegistry() {
        return mEntityRegistry;
    }

}
