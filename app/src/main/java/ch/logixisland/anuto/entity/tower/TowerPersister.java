package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.data.game.TowerDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.EntityPersister;
import ch.logixisland.anuto.entity.plateau.Plateau;

public class TowerPersister implements EntityPersister {

    private final String mEntityName;

    public TowerPersister(String entityName) {
        mEntityName = entityName;
    }

    @Override
    public String getEntityName() {
        return mEntityName;
    }

    @Override
    public EntityDescriptor writeEntityDescriptor(Entity entity, GameEngine gameEngine) {
        Tower tower = (Tower) entity;
        TowerDescriptor towerDescriptor = writeTowerDescriptor(tower, gameEngine);

        towerDescriptor.setName(tower.getEntityName());
        towerDescriptor.setPosition(tower.getPosition());
        towerDescriptor.setValue(tower.getValue());
        towerDescriptor.setLevel(tower.getLevel());
        towerDescriptor.setDamageInflicted(tower.getDamageInflicted());

        return towerDescriptor;
    }

    protected TowerDescriptor writeTowerDescriptor(Tower tower, GameEngine gameEngine) {
        return new TowerDescriptor();
    }

    @Override
    public Entity readEntityDescriptor(EntityRegistry entityRegistry, EntityDescriptor entityDescriptor, GameEngine gameEngine) {
        TowerDescriptor towerDescriptor = (TowerDescriptor) entityDescriptor;
        Tower tower = (Tower) entityRegistry.createEntity(towerDescriptor.getName());

        while (tower.getLevel() < towerDescriptor.getLevel()) {
            tower.enhance();
        }

        tower.setPlateau((Plateau) gameEngine.getEntityById(towerDescriptor.getPlateauId()));
        tower.setValue(towerDescriptor.getValue());
        tower.setDamageInflicted(towerDescriptor.getDamageInflicted());
        tower.setEnabled(true);

        readTowerDescriptor(tower, towerDescriptor, gameEngine);

        return tower;
    }

    protected void readTowerDescriptor(Tower tower, TowerDescriptor towerDescriptor, GameEngine gameEngine) {

    }
}
