package ch.logixisland.anuto.entity.tower;

public interface TowerListener {
    void damageInflicted(float totalDamage);

    void valueChanged(int value);
}
