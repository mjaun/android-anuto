package ch.logixisland.anuto.game.entity.tower;

public interface TowerListener {
    void damageInflicted(float totalDamage);
    void valueChanged(int value);
}
