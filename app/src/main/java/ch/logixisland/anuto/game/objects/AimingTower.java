package ch.logixisland.anuto.game.objects;

public abstract class AimingTower extends Tower {

    public enum Strategy {
        Closest,
        Weakest,
        Strongest,
        First,
        Last
    }

    /*
    ------ Static ------
     */

    private static Strategy sDefaultStrategy = Strategy.Closest;
    private static boolean sDefaultLockTarget = true;

    /*
    ------ Members ------
     */

    private Enemy mTarget = null;
    private Strategy mStrategy = sDefaultStrategy;
    private boolean mLockOnTarget = sDefaultLockTarget;

    /*
    ------ Listener Implementations ------
     */

    private final GameObject.Listener mTargetListener = new Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            onTargetLost();
        }
    };

    /*
    ------ Methods ------
     */

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void clean() {
        super.clean();
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (getGame().tick100ms(this)) {
            if (mTarget != null && getDistanceTo(mTarget) > getRange()) {
                onTargetLost();
            }

            if (mTarget == null || !mLockOnTarget) {
                nextTarget();
            }
        }
    }


    public Strategy getStrategy() {
        return mStrategy;
    }

    public void setStrategy(Strategy strategy) {
        mStrategy = strategy;
        sDefaultStrategy = strategy;
    }

    public boolean doesLockOnTarget() {
        return mLockOnTarget;
    }

    public void setLockOnTarget(boolean lock) {
        mLockOnTarget = lock;
        sDefaultLockTarget = lock;
    }


    @Override
    public Tower upgrade() {
        Tower upgrade = super.upgrade();

        if (upgrade instanceof AimingTower) {
            AimingTower aiming = (AimingTower)upgrade;
            aiming.mStrategy = this.mStrategy;
            aiming.mLockOnTarget = this.mLockOnTarget;
        }

        return upgrade;
    }

    public Enemy getTarget() {
        return mTarget;
    }

    protected void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(mTargetListener);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(mTargetListener);
        }
    }

    protected void nextTarget() {
        switch (mStrategy) {
            case Closest:
                setTarget(getPossibleTargets().min(distanceTo(getPosition())));
                break;

            case Strongest:
                setTarget(getPossibleTargets().max(Enemy.health()));
                break;

            case Weakest:
                setTarget(getPossibleTargets().min(Enemy.health()));
                break;

            case First:
                setTarget(getPossibleTargets().min(Enemy.distanceRemaining()));
                break;

            case Last:
                setTarget(getPossibleTargets().max(Enemy.distanceRemaining()));
        }
     }

    protected void onTargetLost() {
        setTarget(null);
    }
}
