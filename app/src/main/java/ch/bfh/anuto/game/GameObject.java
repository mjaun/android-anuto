package ch.bfh.anuto.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import org.simpleframework.xml.Attribute;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.util.RemovedMark;

public abstract class GameObject implements RemovedMark {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onAddObject(GameObject obj);
        void onRemoveObject(GameObject obj);
    }

    /*
    ------ Members ------
     */

    protected final PointF mPosition = new PointF();
    protected Sprite mSprite = null;

    protected GameEngine mGame = null;
    private boolean mMarkedAsRemoved = false;

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Methods ------
     */

    public abstract int getTypeId();

    public abstract void init(Resources res);

    public void clean() {
    }

    public void tick() {
    }

    public void draw(Canvas canvas) {
        mSprite.draw(canvas);
    }

    public void remove() {
        if (mGame != null) {
            mGame.removeObject(this);
        }
    }


    public GameEngine getGame() {
        return mGame;
    }

    public void setGame(GameEngine game) {
        mGame = game;

        if (game != null) {
            onAdd();
        } else {
            onRemove();
        }
    }


    public PointF getPosition() {
        return mPosition;
    }

    public void setPosition(PointF position) {
        mPosition.x = position.x;
        mPosition.y = position.y;
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


    @Override
    public void resetRemovedMark() {
        mMarkedAsRemoved = false;
    }

    @Override
    public void markAsRemoved() {
        mMarkedAsRemoved = true;
    }

    @Override
    public boolean hasRemovedMark() {
        return mMarkedAsRemoved;
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

    protected void onAdd() {
        for (Listener l : mListeners) {
            l.onAddObject(this);
        }
    }

    protected void onRemove() {
        for (Listener l : mListeners) {
            l.onRemoveObject(this);
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
        mPosition.x = x;
    }

    @Attribute(name="y")
    private float getPositionY() {
        return mPosition.y;
    }

    @Attribute(name="y")
    private void setPositionY(float y) {
        mPosition.y = y;
    }
}
