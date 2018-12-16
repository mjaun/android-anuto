package ch.logixisland.anuto;

import java.util.Random;

import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.business.tower.TowerControl;
import ch.logixisland.anuto.business.tower.TowerInserter;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.business.wave.WaveManager;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class DefaultGameSimulator extends GameSimulator {

    private static final int FINAL_TIER = 3;
    private static final int MAX_UNENHANCED_TOWERS_PER_TIER = 2;
    private static final int MAX_ENHANCED_TOWERS_PER_TIER = 4;
    private static final int NO_SLOW_DOWN_TOWERS_BEFORE_WAVE = 20;

    private Random mRandom = new Random();

    @Override
    protected void tick() {
        tryUpgradeTower();
        tryEnhanceTower();
        tryBuildTower();
        tryStartNextWave();
    }

    private void tryUpgradeTower() {
        final ScoreBoard scoreBoard = getGameFactory().getScoreBoard();
        final TowerSelector towerSelector = getGameFactory().getTowerSelector();
        final TowerControl towerControl = getGameFactory().getTowerControl();

        StreamIterator<Tower> iterator = getTowers();
        while (iterator.hasNext()) {
            Tower tower = iterator.next();
            final int tier = getTowerTier(tower);

            if (!tower.isUpgradeable() || tower.getUpgradeCost() > scoreBoard.getCredits()) {
                continue;
            }

            if (tower.getLevel() > 1) {
                continue;
            }

            int unenhancedTowerCount = getTowers()
                    .filter(new Predicate<Tower>() {
                        @Override
                        public boolean apply(Tower tower) {
                            return getTowerTier(tower) == tier + 1 && tower.getLevel() == 1;
                        }
                    })
                    .count();

            if (unenhancedTowerCount > MAX_ENHANCED_TOWERS_PER_TIER && tier + 1 != FINAL_TIER) {
                continue;
            }

            towerSelector.selectTower(tower);
            towerControl.upgradeTower();
        }
    }

    private void tryEnhanceTower() {
        final ScoreBoard scoreBoard = getGameFactory().getScoreBoard();

        StreamIterator<Tower> iterator = getTowers();
        while (iterator.hasNext()) {
            Tower tower = iterator.next();
            final int tier = getTowerTier(tower);

            if (!tower.isEnhanceable() || tower.getEnhanceCost() > scoreBoard.getCredits()) {
                continue;
            }

            int enhancedTowerCount = getTowers()
                    .filter(new Predicate<Tower>() {
                        @Override
                        public boolean apply(Tower tower) {
                            return getTowerTier(tower) == tier && tower.getLevel() > 1;
                        }
                    })
                    .count();

            if (enhancedTowerCount > MAX_ENHANCED_TOWERS_PER_TIER) {
                continue;
            }

            tower.enhance();
            return;
        }
    }

    private void tryBuildTower() {
        final TowerInserter towerInserter = getGameFactory().getTowerInserter();
        final ScoreBoard scoreBoard = getGameFactory().getScoreBoard();
        final WaveManager waveManager = getGameFactory().getWaveManager();

        int unenhancedTowerCount = getTowers()
                .filter(new Predicate<Tower>() {
                    @Override
                    public boolean apply(Tower tower) {
                        return getTowerTier(tower) == 1 && tower.getLevel() == 1;
                    }
                })
                .count();

        if (unenhancedTowerCount >= MAX_UNENHANCED_TOWERS_PER_TIER) {
            return;
        }

        Tower selectedTower = getBuildableTowers()
                .filter(new Predicate<Tower>() {
                    @Override
                    public boolean apply(Tower tower) {
                        return tower.getValue() <= scoreBoard.getCredits() && (tower.getDamage() > 0 || waveManager.getWaveNumber() > NO_SLOW_DOWN_TOWERS_BEFORE_WAVE);
                    }
                })
                .random(mRandom);

        if (selectedTower == null) {
            return;
        }

        Plateau selectedPlateau = getFreePlateaus().random(mRandom);

        if (selectedPlateau == null) {
            return;
        }

        towerInserter.insertTower(selectedTower.getEntityName());
        towerInserter.setPosition(selectedPlateau.getPosition());
        towerInserter.buyTower();
    }

    private void tryStartNextWave() {
        final WaveManager waveManager = getGameFactory().getWaveManager();

        if (waveManager.isNextWaveReady() && waveManager.getRemainingEnemiesCount() < 50) {
            waveManager.startNextWave();
        }
    }

}
