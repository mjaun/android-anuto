package ch.bfh.anuto.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.simpleframework.xml.Attribute;

import java.util.ArrayList;
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

    protected PointF mPosition = null;
    protected GameEngine mGame = null;
    protected Sprite mSprite = null;

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Methods ------
     */

    public abstract void initResources(Resources res);

    public abstract int getLayer();

    public void tick() {

    }

    public void draw(Canvas canvas) {
        mSprite.draw(canvas);
    }

    public boolean isRemoved() {
        return mGame == null;
    }

    public void remove() {
        if (!isRemoved()) {
            mGame.removeObject(this);
        }
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

    public void move(float dx, float dy) {
        mPosition.x += dx;
        mPosition.y += dy;
    }

    public void move(PointF direction, float distance) {
        mPosition.x += direction.x * distance;
        mPosition.y += direction.y * distance;
    }

    public float getDistanceTo(PointF target) {
        return (float)Math.sqrt(Math.pow(target.x - mPosition.x, 2) + Math.pow(target.y - mPosition.y, 2));
    }

    public PointF getDirectionTo(PointF target) {
        float dist = getDistanceTo(target);
        return new PointF((target.x - mPosition.x) / dist, (target.y - mPosition.y) / dist);
    }

    public float getAngleTo(PointF target) {
        return (float)Math.atan2(target.x - mPosition.x, mPosition.y - target.y) / (float)Math.PI * 180f;
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
