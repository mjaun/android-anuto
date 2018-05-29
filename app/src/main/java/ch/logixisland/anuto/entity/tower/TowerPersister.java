package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.data.state.EntityData;
import ch.logixisland.anuto.data.state.TowerData;
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
    protected TowerData createEntityDescriptor() {
        return new TowerData();
    }

    @Override
    protected TowerData writeEntityDescriptor(Entity entity) {
        Tower tower = (Tower) entity;
        TowerData descriptor = (TowerData) super.writeEntityDescriptor(tower);

        descriptor.setPlateauId(tower.getPlateau().getEntityId());
        descriptor.setValue(tower.getValue());
        descriptor.setLevel(tower.getLevel());
        descriptor.setDamageInflicted(tower.getDamageInflicted());

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            descriptor.setStrategy(aimer.getStrategy().toString());
            descriptor.setLockTarget(aimer.doesLockTarget());
        }

        return descriptor;
    }

    @Override
    protected Tower readEntityDescriptor(EntityData entityData) {
        Tower tower = (Tower) super.readEntityDescriptor(entityData);
        TowerData descriptor = (TowerData) entityData;

        while (tower.getLevel() < descriptor.getLevel()) {
            tower.enhance();
        }

        tower.setPlateau((Plateau) getGameEngine().getEntityById(descriptor.getPlateauId()));
        tower.setValue(descriptor.getValue());
        tower.setDamageInflicted(descriptor.getDamageInflicted());
        tower.setEnabled(true);

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            aimer.setStrategy(TowerStrategy.valueOf(descriptor.getStrategy()));
            aimer.setLockTarget(descriptor.isLockTarget());
        }

        return tower;
    }

}
