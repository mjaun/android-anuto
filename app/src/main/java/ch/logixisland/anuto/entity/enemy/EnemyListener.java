package ch.logixisland.anuto.entity.enemy;

public interface EnemyListener {
    void enemyKilled(Enemy enemy);

    void enemyFinished(Enemy enemy);

    void enemyRemoved(Enemy enemy);
}
