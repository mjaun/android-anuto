package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.bfh.anuto.game.DrawObject;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.TypeIds;
import ch.bfh.anuto.util.iterator.StreamIterator;

public abstract class Tower extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.TOWER;

    /*
    ------ RangeIndicator Class ------
     */

    private class RangeIndicator extends DrawObject {
        private Paint mRangeIndicatorPen;

        public RangeIndicator() {
            mRangeIndicatorPen = new Paint();
            mRangeIndicatorPen.setStyle(Paint.Style.STROKE);
            mRangeIndicatorPen.setStrokeWidth(0.05f);
            mRangeIndicatorPen.setColor(Color.GREEN);
            mRangeIndicatorPen.setAlpha(128);
        }

        @Override
        public int getLayer() {
            return Layers.TOWER_RANGE;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(mPosition.x, mPosition.y, mRange, mRangeIndicatorPen);
        }
    }

    /*
    ------ Members ------
     */

    protected float mRange;
    protected float mReloadTime;
    protected boolean mReloaded = false;
    protected boolean mEnabled = false;
    protected Plateau mPlateau = null;

    private TickTimer mReloadTimer;
    private RangeIndicator mRangeIndicator;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init() {
        super.init();
        mReloadTimer = TickTimer.createInterval(mReloadTime);
    }

    @Override
    public void clean() {
        super.clean();
        hideRange();
        setPosition((Plateau)null);
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled && !mReloaded && mReloadTimer.tick()) {
            mReloaded = true;
        }
    }


    public Plateau getPlateau() {
        return mPlateau;
    }

    public void setPosition(Plateau plateau) {
        if (mPlateau != null) {
            mPlateau.setOccupant(null);
        }

        mPlateau = plateau;

        if (mPlateau != null) {
            mPlateau.setOccupant(this);
            setPosition(mPlateau.getPosition());
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public void showRange() {
        if (mRangeIndicator == null) {
            mRangeIndicator = new RangeIndicator();
            mGame.add(mRangeIndicator);
        }
    }

    public void hideRange() {
        if (mRangeIndicator != null) {
            mGame.remove(mRangeIndicator);
            mRangeIndicator = null;
        }
    }


    protected void shoot(Shot shot) {
        mGame.add(shot);
        mReloaded = false;
    }

    protected void shoot(AreaEffect effect) {
        mGame.add(effect);
        mReloaded = false;
    }

    protected StreamIterator<Enemy> getEnemiesInRange() {
        return mGame.getGameObjects(Enemy.TYPE_ID)
                .filter(GameObject.inRange(mPosition, mRange))
                .cast(Enemy.class);
    }
}
