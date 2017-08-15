package ch.logixisland.anuto.entity.tower;

import java.util.List;

import ch.logixisland.anuto.data.map.PathDescriptor;
import ch.logixisland.anuto.data.setting.tower.TowerSettingsRoot;
import ch.logixisland.anuto.data.setting.tower.TowerSlots;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class TowerFactory {

    private final GameEngine mGameEngine;

    public TowerFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public String getSlotTowerName(int slot) {
        TowerSettingsRoot towerSettingsRoot = mGameEngine.getGameConfiguration().getTowerSettingsRoot();
        TowerSlots towerSlots = towerSettingsRoot.getTowerSlots();

        switch (slot) {
            case 0:
                return towerSlots.getSlot1();

            case 1:
                return towerSlots.getSlot2();

            case 2:
                return towerSlots.getSlot3();

            case 3:
                return towerSlots.getSlot4();

            default:
                throw new IllegalArgumentException("Illegal slot index!");
        }
    }

    public int getTowerValue(String name) {
        TowerSettingsRoot towerSettingsRoot = mGameEngine.getGameConfiguration().getTowerSettingsRoot();

        switch (name) {
            case "canon":
                return towerSettingsRoot.getCanonSettings().getValue();

            case "dualCanon":
                return towerSettingsRoot.getDualCanonSettings().getValue();

            case "machineGun":
                return towerSettingsRoot.getMachineGunSettings().getValue();

            case "simpleLaser":
                return towerSettingsRoot.getSimpleLaserSettings().getValue();

            case "bouncingLaser":
                return towerSettingsRoot.getBouncingLaserSettings().getValue();

            case "straightLaser":
                return towerSettingsRoot.getStraightLaserSettings().getValue();

            case "mortar":
                return towerSettingsRoot.getMortarSettings().getValue();

            case "mineLayer":
                return towerSettingsRoot.getMineLayerSettings().getValue();

            case "rocketLauncher":
                return towerSettingsRoot.getRocketLauncherSettings().getValue();

            case "glueTower":
                return towerSettingsRoot.getGlueTowerSettings().getValue();

            case "glueGun":
                return towerSettingsRoot.getGlueGunSettings().getValue();

            case "teleporter":
                return towerSettingsRoot.getTeleporterSettings().getValue();

            default:
                throw new IllegalArgumentException("Tower name not known!");
        }
    }

    public Tower createTower(String name) {
        TowerSettingsRoot towerSettingsRoot = mGameEngine.getGameConfiguration().getTowerSettingsRoot();
        List<PathDescriptor> paths = mGameEngine.getGameConfiguration().getMapDescriptorRoot().getPaths();

        switch (name) {
            case "canon":
                return new Canon(mGameEngine, towerSettingsRoot.getCanonSettings());

            case "dualCanon":
                return new DualCanon(mGameEngine, towerSettingsRoot.getDualCanonSettings());

            case "machineGun":
                return new MachineGun(mGameEngine, towerSettingsRoot.getMachineGunSettings());

            case "simpleLaser":
                return new SimpleLaser(mGameEngine, towerSettingsRoot.getSimpleLaserSettings());

            case "bouncingLaser":
                return new BouncingLaser(mGameEngine, towerSettingsRoot.getBouncingLaserSettings());

            case "straightLaser":
                return new StraightLaser(mGameEngine, towerSettingsRoot.getStraightLaserSettings());

            case "mortar":
                return new Mortar(mGameEngine, towerSettingsRoot.getMortarSettings());

            case "mineLayer":
                return new MineLayer(mGameEngine, towerSettingsRoot.getMineLayerSettings(), paths);

            case "rocketLauncher":
                return new RocketLauncher(mGameEngine, towerSettingsRoot.getRocketLauncherSettings());

            case "glueTower":
                return new GlueTower(mGameEngine, towerSettingsRoot.getGlueTowerSettings(), paths);

            case "glueGun":
                return new GlueGun(mGameEngine, towerSettingsRoot.getGlueGunSettings());

            case "teleporter":
                return new Teleporter(mGameEngine, towerSettingsRoot.getTeleporterSettings());

            default:
                throw new IllegalArgumentException("Tower name not known!");
        }
    }

}
