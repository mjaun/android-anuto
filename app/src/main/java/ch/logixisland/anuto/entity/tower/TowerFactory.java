package ch.logixisland.anuto.entity.tower;

import java.util.List;

import ch.logixisland.anuto.data.map.PathDescriptor;
import ch.logixisland.anuto.data.tower.TowerSettingsRoot;
import ch.logixisland.anuto.data.tower.TowerSlots;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class TowerFactory {

    private final GameEngine mGameEngine;

    private TowerSettingsRoot mTowerSettingsRoot;
    private List<PathDescriptor> mPaths;

    public TowerFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void setTowerSettingsRoot(TowerSettingsRoot towerSettingsRoot) {
        mTowerSettingsRoot = towerSettingsRoot;
    }

    public void setPaths(List<PathDescriptor> paths) {
        mPaths = paths;
    }

    public String getSlotTowerName(int slot) {
        TowerSlots towerSlots = mTowerSettingsRoot.getTowerSlots();

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
        switch (name) {
            case "canon":
                return mTowerSettingsRoot.getCanonSettings().getValue();

            case "dualCanon":
                return mTowerSettingsRoot.getDualCanonSettings().getValue();

            case "machineGun":
                return mTowerSettingsRoot.getMachineGunSettings().getValue();

            case "simpleLaser":
                return mTowerSettingsRoot.getSimpleLaserSettings().getValue();

            case "bouncingLaser":
                return mTowerSettingsRoot.getBouncingLaserSettings().getValue();

            case "straightLaser":
                return mTowerSettingsRoot.getStraightLaserSettings().getValue();

            case "mortar":
                return mTowerSettingsRoot.getMortarSettings().getValue();

            case "mineLayer":
                return mTowerSettingsRoot.getMineLayerSettings().getValue();

            case "rocketLauncher":
                return mTowerSettingsRoot.getRocketLauncherSettings().getValue();

            case "glueTower":
                return mTowerSettingsRoot.getGlueTowerSettings().getValue();

            case "glueGun":
                return mTowerSettingsRoot.getGlueGunSettings().getValue();

            case "teleporter":
                return mTowerSettingsRoot.getTeleporterSettings().getValue();

            default:
                throw new IllegalArgumentException("Tower name not known!");
        }
    }

    public Tower createTower(String name) {
        switch (name) {
            case "canon":
                return new Canon(mGameEngine, mTowerSettingsRoot.getCanonSettings());

            case "dualCanon":
                return new DualCanon(mGameEngine, mTowerSettingsRoot.getDualCanonSettings());

            case "machineGun":
                return new MachineGun(mGameEngine, mTowerSettingsRoot.getMachineGunSettings());

            case "simpleLaser":
                return new SimpleLaser(mGameEngine, mTowerSettingsRoot.getSimpleLaserSettings());

            case "bouncingLaser":
                return new BouncingLaser(mGameEngine, mTowerSettingsRoot.getBouncingLaserSettings());

            case "straightLaser":
                return new StraightLaser(mGameEngine, mTowerSettingsRoot.getStraightLaserSettings());

            case "mortar":
                return new Mortar(mGameEngine, mTowerSettingsRoot.getMortarSettings());

            case "mineLayer":
                return new MineLayer(mGameEngine, mTowerSettingsRoot.getMineLayerSettings(), mPaths);

            case "rocketLauncher":
                return new RocketLauncher(mGameEngine, mTowerSettingsRoot.getRocketLauncherSettings());

            case "glueTower":
                return new GlueTower(mGameEngine, mTowerSettingsRoot.getGlueTowerSettings(), mPaths);

            case "glueGun":
                return new GlueGun(mGameEngine, mTowerSettingsRoot.getGlueGunSettings());

            case "teleporter":
                return new Teleporter(mGameEngine, mTowerSettingsRoot.getTeleporterSettings());

            default:
                throw new IllegalArgumentException("Tower name not known!");
        }
    }

}
