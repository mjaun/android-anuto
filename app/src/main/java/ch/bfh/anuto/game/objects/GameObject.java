package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;

import org.simpleframework.xml.Attribute;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.util.iterator.Function;
import ch.bfh.anuto.util.iterator.Predicate;
import ch.bfh.anuto.util.math.Vector2;

public abstract class GameObject implements Sprite.Listener {

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onObjectAdded(GameObject obj);
        void onObjectRemoved(GameObject obj);
    }

    /*
    ------ Static ------
     */

    public static Predicate<GameObject> inRange(final Vector2 center, final float range) {
        return new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                return value.getDistanceTo(center) <= range;
            }
        };
    }

    public static Predicate<GameObject> onLine(final Vector2 p1, final Vector2 p2, final float lineWidth) {
        final Vector2 line = p2.copy().sub(p1);
        final float lineLen2 = line.len2();
        final float lineLen = (float)Math.sqrt(lineLen2);
        final float lineAngle = line.angle();

        return new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                Vector2 toObj = value.mPosition.copy().sub(p1);

                float angle = toObj.angle() - lineAngle;

                if (Math.abs(Vector2.normalizeAngle(angle)) > 90) {
                    return false;
                }

                Vector2 proj = line.copy().mul(toObj.dot(line) / lineLen2);

                if (proj.len() > lineLen) {
                    return false;
                }

                float dist = toObj.sub(proj).len();

                return dist <= lineWidth / 2f;
            }
        };
    }

    public static Function<GameObject, Float> distanceTo(final Vector2 toPoint) {
        return new Function<GameObject, Float>() {
            @Override
            public Float apply(GameObject input) {
                return input.getDistanceTo(toPoint);
            }
        };
    }

    /*
    ------ Members ------
     */

    private boolean mActive = false;
    protected boolean mEnabled = true;
    protected final Vector2 mPosition = new Vector2();
    protected final GameEngine mGame = GameEngine.getInstance();

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Methods ------
     */

    public abstract int getTypeId();


    public void onInit() {
        mActive = true;

        for (Listener l : mListeners) {
            l.onObjectAdded(this);
        }
    }

    public void onClean() {
        mActive = false;

        for (Listener l : mListeners) {
            l.onObjectRemoved(this);
        }
    }

    public void onTick() {

    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        canvas.translate(mPosition.x, mPosition.y);
    }


    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isActive() {
        return mActive;
    }

    public void remove() {
        mGame.remove(this);
    }


    public Vector2 getPosition() {
        return mPosition;
    }

    public void setPosition(float x, float y) {
        mPosition.set(x, y);
    }

    public void setPosition(Vector2 position) {
        mPosition.set(position);
    }


    public void move(float dx, float dy) {
        mPosition.x += dx;
        mPosition.y += dy;
    }

    public void move(Vector2 offset) {
        mPosition.x += offset.x;
        mPosition.y += offset.y;
    }

    public void move(Vector2 direction, float distance) {
        mPosition.x += direction.x * distance;
        mPosition.y += direction.y * distance;
    }

    public void moveSpeed(Vector2 direction, float speed) {
        mPosition.x += direction.x * speed / GameEngine.TARGET_FRAME_RATE;
        mPosition.y += direction.y * speed / GameEngine.TARGET_FRAME_RATE;
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


    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    /*
    ------ XML Serialization ------
     */

    @Attribute(name="x", required=false)
    private float getPositionX() {
        return mPosition.x;
    }

    @Attribute(name="x", required=false)
    private void setPositionX(float x) {
        mPosition.x = x;
    }

    @Attribute(name="y", required=false)
    private float getPositionY() {
        return mPosition.y;
    }

    @Attribute(name="y", required=false)
    private void setPositionY(float y) {
        mPosition.y = y;
    }
}
