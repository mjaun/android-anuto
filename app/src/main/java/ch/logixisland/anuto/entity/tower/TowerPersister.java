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
        TowerDescriptor descriptor = (TowerDescriptor) super.writeEntityDescriptor(tower);

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
    protected Tower readEntityDescriptor(EntityDescriptor entityDescriptor) {
        Tower tower = (Tower) super.readEntityDescriptor(entityDescriptor);
        TowerDescriptor descriptor = (TowerDescriptor) entityDescriptor;

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
