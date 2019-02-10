package ch.logixisland.anuto.entity.enemy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class EnemySettings {

    private final int mHealth;
    private final float mSpeed;
    private final float mMinSpeed;
    private final int mReward;
    private final Collection<WeaponType> mWeakAgainst;
    private final Collection<WeaponType> mStrongAgainst;
    private final float mWeakAgainstModifier;
    private final float mStrongAgainstModifier;

    public EnemySettings(int health, float speed, float minSpeed, int reward,
                         WeaponType[] weakAgainst, WeaponType[] strongAgainst,
                         float weakAgainstModifier, float strongAgainstModifier) {
        mHealth = health;
        mSpeed = speed;
        mMinSpeed = minSpeed;
        mReward = reward;
        mWeakAgainst = Arrays.asList(weakAgainst);
        mStrongAgainst = Arrays.asList(strongAgainst);
        mWeakAgainstModifier = weakAgainstModifier;
        mStrongAgainstModifier = strongAgainstModifier;
    }

    public int getHealth() {
        return mHealth;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public float getMinSpeed() {
        return mMinSpeed;
    }

    public int getReward() {
        return mReward;
    }

    public Collection<WeaponType> getWeakAgainst() {
        return Collections.unmodifiableCollection(mWeakAgainst);
    }

    public Collection<WeaponType> getStrongAgainst() {
        return Collections.unmodifiableCollection(mStrongAgainst);
    }

    public float getWeakAgainstModifier() {
        return mWeakAgainstModifier;
    }

    public float getStrongAgainstModifier() {
        return mStrongAgainstModifier;
    }
}
