package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityPersister;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class TowerPersister extends EntityPersister {

    @Override
    public KeyValueStore writeEntityData(Entity entity) {
        Tower tower = (Tower) entity;

        if (!tower.isBuilt()) {
            return null;
        }

        KeyValueStore data = super.writeEntityData(tower);

        data.putInt("plateauId", tower.getPlateau().getEntityId());
        data.putInt("value", tower.getValue());
        data.putInt("level", tower.getLevel());
        data.putFloat("damageInflicted", tower.getDamageInflicted());

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            data.putString("strategy", aimer.getStrategy().toString());
            data.putBoolean("lockTarget", aimer.doesLockTarget());
        }

        return data;
    }

    @Override
    public void readEntityData(Entity entity, KeyValueStore entityData) {
        super.readEntityData(entity, entityData);
        Tower tower = (Tower) entity;

        while (tower.getLevel() < entityData.getInt("level")) {
            tower.enhance();
        }

        tower.setPlateau((Plateau) tower.getGameEngine().getEntityById(entityData.getInt("plateauId")));
        tower.setValue(entityData.getInt("value"));
        tower.setDamageInflicted(entityData.getFloat("damageInflicted"));
        tower.setBuilt();

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            aimer.setStrategy(TowerStrategy.valueOf(entityData.getString("strategy")));
            aimer.setLockTarget(entityData.getBoolean("lockTarget"));
        }
    }

}
