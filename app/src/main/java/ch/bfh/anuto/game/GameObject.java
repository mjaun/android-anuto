package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.simpleframework.xml.Attribute;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GameObject {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onObjectRemove(GameObject obj);
    }

    /*
    ------ Members ------
     */

    protected static final RectF TILE_RECT = new RectF(-0.5f, -0.5f, 0.5f, 0.5f);

    protected GameEngine mGame = null;
    protected Paint mPaint = new Paint();

    protected PointF mPosition = null;

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Methods ------
     */

    public abstract void tick();

    public abstract void draw(Canvas canvas);

    public boolean isRemoved() {
        return mGame != null;
    }

    public void setGame(GameEngine game) {
        if (mGame != null && game == null) {
            mGame = null;
            onRemove();
        }

        mGame = game;
    }

    public PointF getPosition() {
        return mPosition;
    }

    public void setPosition(PointF position) {
        mPosition = new PointF(position.x, position.y);
    }

    public float getDistanceTo(PointF target) {
        return (float)Math.sqrt(Math.pow(target.x - mPosition.x, 2) + Math.pow(target.y - mPosition.y, 2));
    }

    public PointF getDirectionTo(PointF target) {
        float dist = getDistanceTo(target);
        return new PointF((target.x - mPosition.x) / dist, (target.y - mPosition.y) / dist);
    }

    /*
    ------ Listener Stuff ------
     */

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    protected void onRemove() {
        for (Listener l : mListeners) {
            l.onObjectRemove(this);
        }
    }

    /*
    ------ XML Serialization ------
     */

    @Attribute(name="x")
    private float getPositionX() {
        return mPosition.x;
    }

    @Attribute(name="x")
    private void setPositionX(float x) {
        if (mPosition == null) {
            mPosition = new PointF(x, 0);
        }
        else {
            mPosition.x = x;
        }
    }

    @Attribute(name="y")
    private float getPositionY() {
        return mPosition.y;
    }

    @Attribute(name="y")
    private void setPositionY(float y) {
        if (mPosition == null) {
            mPosition = new PointF(0, y);
        }
        else {
            mPosition.y = y;
        }
    }
}
