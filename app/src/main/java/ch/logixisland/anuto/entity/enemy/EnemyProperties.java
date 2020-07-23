package ch.logixisland.anuto.entity.enemy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class EnemyProperties {

    private EnemyType mEnemyType;
    private int mHealth;
    private float mSpeed;
    private int mReward;
    private Collection<WeaponType> mWeakAgainst = Collections.emptyList();
    private Collection<WeaponType> mStrongAgainst = Collections.emptyList();

    public static class Builder {

        private EnemyProperties mResult = new EnemyProperties();

        public Builder(String entityName) {

            this.setEnemyType(EnemyType.valueOf(entityName));

            switch (mResult.getEnemyType()) {
                case soldier:
                    this.setHealth(300)
                            .setSpeed(1.0f)
                            .setReward(10);
                    break;
                case blob:
                    this.setHealth(600)
                            .setSpeed(0.5f)
                            .setReward(20)
                            .setWeakAgainst(WeaponType.Explosive)
                            .setStrongAgainst(WeaponType.Bullet);
                    break;
                case sprinter:
                    this.setHealth(200)
                            .setSpeed(3.0f)
                            .setReward(15)
                            .setWeakAgainst(WeaponType.Explosive)
                            .setStrongAgainst(WeaponType.Laser);
                    break;
                case flyer:
                    this.setHealth(400)
                            .setSpeed(1.3f)
                            .setReward(30)
                            .setWeakAgainst(WeaponType.Laser, WeaponType.Bullet)
                            .setStrongAgainst(WeaponType.Glue);
                    break;
                case healer:
                    this.setHealth(400)
                            .setSpeed(1.2f)
                            .setReward(30)
                            .setWeakAgainst(WeaponType.Laser, WeaponType.Bullet);
                    break;
                default:
                    throw new RuntimeException("Unknown enemy!");
            }
        }

        private Builder setEnemyType(EnemyType enemyType) {
            mResult.mEnemyType = enemyType;
            return this;
        }

        private Builder setHealth(int health) {
            mResult.mHealth = health;
            return this;
        }

        private Builder setSpeed(float speed) {
            mResult.mSpeed = speed;
            return this;
        }

        private Builder setReward(int reward) {
            mResult.mReward = reward;
            return this;
        }

        private Builder setWeakAgainst(WeaponType... weakAgainst) {
            mResult.mWeakAgainst = Arrays.asList(weakAgainst);
            return this;
        }

        private Builder setStrongAgainst(WeaponType... strongAgainst) {
            mResult.mStrongAgainst = Arrays.asList(strongAgainst);
            return this;
        }

        public EnemyProperties build() {
            return mResult;
        }

    }

    public EnemyType getEnemyType() { return mEnemyType; }

    public int getHealth() { return mHealth; }

    public float getSpeed() { return mSpeed; }

    public int getReward() {
        return mReward;
    }

    public Collection<WeaponType> getWeakAgainst() {
        return Collections.unmodifiableCollection(mWeakAgainst);
    }

    public Collection<WeaponType> getStrongAgainst() {
        return Collections.unmodifiableCollection(mStrongAgainst);
    }

}
