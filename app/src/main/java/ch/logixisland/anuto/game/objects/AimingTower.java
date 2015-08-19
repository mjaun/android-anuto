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
    ------ Members ------
     */

    protected Enemy mTarget = null;
    protected Strategy mStrategy = Strategy.Closest;
    protected boolean mLockOnTarget = true;

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

        if (mGame.tick100ms(this)) {
            if (mTarget != null && getDistanceTo(mTarget) > mConfig.range) {
                onTargetLost();
            }

            if (mTarget == null || !mLockOnTarget) {
                nextTarget();
            }
        }
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
                setTarget(getPossibleTargets().min(GameObject.distanceTo(mPosition)));
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
