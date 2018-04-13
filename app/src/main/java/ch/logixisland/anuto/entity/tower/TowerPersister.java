package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.data.entity.TowerDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.EntityPersister;
import ch.logixisland.anuto.entity.plateau.Plateau;

public class TowerPersister extends EntityPersister {

    public TowerPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        super(gameEngine, entityRegistry, entityName);
    }

    @Override
    protected TowerDescriptor createEntityDescriptor() {
        return new TowerDescriptor();
    }

    @Override
    protected TowerDescriptor writeEntityDescriptor(Entity entity) {
        Tower tower = (Tower) entity;
        TowerDescriptor towerDescriptor = (TowerDescriptor) super.writeEntityDescriptor(tower);

        towerDescriptor.setId(tower.getEntityId());
        towerDescriptor.setName(tower.getEntityName());
        towerDescriptor.setPosition(tower.getPosition());
        towerDescriptor.setValue(tower.getValue());
        towerDescriptor.setLevel(tower.getLevel());
        towerDescriptor.setDamageInflicted(tower.getDamageInflicted());

        return towerDescriptor;
    }

    @Override
    protected Tower readEntityDescriptor(EntityDescriptor entityDescriptor) {
        Tower tower = (Tower) super.readEntityDescriptor(entityDescriptor);
        TowerDescriptor towerDescriptor = (TowerDescriptor) entityDescriptor;

        while (tower.getLevel() < towerDescriptor.getLevel()) {
            tower.enhance();
        }

        tower.setPlateau((Plateau) getGameEngine().getEntityById(towerDescriptor.getPlateauId()));
        tower.setValue(towerDescriptor.getValue());
        tower.setDamageInflicted(towerDescriptor.getDamageInflicted());
        tower.setEnabled(true);

        return tower;
    }

}
