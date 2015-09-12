package ch.logixisland.anuto.game.objects;

import android.graphics.Canvas;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.math.Vector2;

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

    public static Predicate<GameObject> inGame() {
        return new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                return value.isInGame();
            }
        };
    }

    public static Predicate<GameObject> enabled() {
        return new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                return value.isEnabled();
            }
        };
    }

    public static Predicate<GameObject> inRange(final Vector2 center, final float range) {
        return new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                return value.getDistanceTo(center) <= range;
            }
        };
    }

    public static Predicate<GameObject> onLine(final Vector2 p1, final Vector2 p2, final float lineWidth) {
        return new Predicate<GameObject>() {
            @Override
            public boolean apply(GameObject value) {
                Vector2 line = Vector2.fromTo(p1, p2);
                Vector2 toObj = Vector2.fromTo(p1, value.mPosition);

                Vector2 proj = toObj.copy().proj(line);

                if (proj.len() > line.len()) {
                    return false;
                }

                float angle = toObj.angle() - line.angle();

                if (Math.abs(Vector2.normalizeAngle(angle)) > 90f) {
                    return false;
                }

                return toObj.sub(proj).len() <= lineWidth / 2f;

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

    private boolean mInGame = false;
    private boolean mEnabled = true;

    private final Vector2 mPosition = new Vector2();

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    /*
    ------ Methods ------
     */

    public abstract int getTypeId();


    public GameEngine.StaticData initStatic() {
        return null;
    }

    public void init() {
        mInGame = true;

        for (Listener l : mListeners) {
            l.onObjectAdded(this);
        }
    }

    public void clean() {
        mInGame = false;

        for (Listener l : mListeners) {
            l.onObjectRemoved(this);
        }
    }


    public void tick() {

    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        canvas.translate(mPosition.x, mPosition.y);
    }


    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }


    protected GameEngine getGame() {
        return GameEngine.getInstance();
    }

    protected GameManager getManager() {
        return GameManager.getInstance();
    }

    protected GameEngine.StaticData getStaticData() {
        return getGame().getStaticData(this);
    }

    public boolean isInGame() {
        return mInGame;
    }

    public void remove() {
        getGame().remove(this);
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


    public float getDistanceTo(GameObject target) {
        return getDistanceTo(target.mPosition);
    }

    public float getDistanceTo(Vector2 target) {
        return Vector2.fromTo(mPosition, target).len();
    }

    public Vector2 getDirectionTo(GameObject target) {
        return getDirectionTo(target.mPosition);
    }

    public Vector2 getDirectionTo(Vector2 target) {
        return Vector2.fromTo(mPosition, target).norm();
    }

    public float getAngleTo(GameObject target) {
        return getAngleTo(target.mPosition);
    }

    public float getAngleTo(Vector2 target) {
        return Vector2.fromTo(mPosition, target).angle();
    }


    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }
}
