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
    protected TowerData createEntityData() {
        return new TowerData();
    }

    @Override
    protected TowerData writeEntityData(Entity entity) {
        Tower tower = (Tower) entity;
        TowerData data = (TowerData) super.writeEntityData(tower);

        data.setPlateauId(tower.getPlateau().getEntityId());
        data.setValue(tower.getValue());
        data.setLevel(tower.getLevel());
        data.setDamageInflicted(tower.getDamageInflicted());

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            data.setStrategy(aimer.getStrategy().toString());
            data.setLockTarget(aimer.doesLockTarget());
        }

        return data;
    }

    @Override
    protected Tower readEntityData(EntityData entityData) {
        Tower tower = (Tower) super.readEntityData(entityData);
        TowerData data = (TowerData) entityData;

        while (tower.getLevel() < data.getLevel()) {
            tower.enhance();
        }

        tower.setPlateau((Plateau) getGameEngine().getEntityById(data.getPlateauId()));
        tower.setValue(data.getValue());
        tower.setDamageInflicted(data.getDamageInflicted());
        tower.setEnabled(true);

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            aimer.setStrategy(TowerStrategy.valueOf(data.getStrategy()));
            aimer.setLockTarget(data.isLockTarget());
        }

        return tower;
    }

}
