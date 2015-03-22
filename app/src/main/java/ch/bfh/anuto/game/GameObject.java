package ch.bfh.anuto.game;

import android.content.res.Resources;
import android.graphics.Canvas;

import org.simpleframework.xml.Attribute;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.util.Function;
import ch.bfh.anuto.util.Iterators;
import ch.bfh.anuto.util.Predicate;
import ch.bfh.anuto.util.RemovedMark;
import ch.bfh.anuto.util.Vector2;

public abstract class GameObject implements RemovedMark {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onAddObject(GameObject obj);
        void onRemoveObject(GameObject obj);
    }

    /*
    ------ Static ------
     */

    public static <T extends GameObject> Iterator<T> inRange(Iterator<T> objects, final Vector2 center, final float range) {
        return Iterators.filter(objects, new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                return value.getDistanceTo(center) <= range;
            }
        });
    }

    public static <T extends GameObject> Iterator<T> onLine(Iterator<T> objects, final Vector2 p1, final Vector2 p2, final float lineWidth) {
        final Vector2 line = p2.copy().sub(p1);
        final float lineLen2 = line.len2();

        return Iterators.filter(objects, new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                Vector2 toObj = value.mPosition.copy().sub(p1);
                Vector2 proj = line.copy().mul(toObj.dot(line) / lineLen2);

                float dist = toObj.sub(proj).len();
                return dist <= lineWidth;
            }
        });
    }

    public static <T extends GameObject> T closest(Iterator<T> objects, final Vector2 point) {
        return Iterators.min(objects, new Function<T, Float>() {
            @Override
            public Float apply(GameObject input) {
                return input.getDistanceTo(point);
            }
        });
    }

    /*
    ------ Members ------
     */

    protected final Vector2 mPosition = new Vector2();
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


    public Vector2 getPosition() {
        return mPosition;
    }

    public void setPosition(Vector2 position) {
        mPosition.set(position);
    }

    public void setPosition(float x, float y) {
        mPosition.x = x;
        mPosition.y = y;
    }


    public void move(float dx, float dy) {
        mPosition.x += dx;
        mPosition.y += dy;
    }

    public void move(Vector2 direction, float distance) {
        mPosition.x += direction.x * distance;
        mPosition.y += direction.y * distance;
    }


    public float getDistanceTo(GameObject target) {
        return getDistanceTo(target.mPosition);
    }

    public float getDistanceTo(Vector2 target) {
        return target.copy().sub(mPosition).len();
    }

    public Vector2 getDirectionTo(GameObject target) {
        return getDirectionTo(target.mPosition);
    }

    public Vector2 getDirectionTo(Vector2 target) {
        return target.copy().sub(mPosition).norm();
    }

    public float getAngleTo(GameObject target) {
        return getAngleTo(target.mPosition);
    }

    public float getAngleTo(Vector2 target) {
        return target.copy().sub(mPosition).angle();
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
