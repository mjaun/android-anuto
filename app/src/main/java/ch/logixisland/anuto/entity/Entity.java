package ch.logixisland.anuto.entity;

import android.graphics.Canvas;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.TickListener;
import ch.logixisland.anuto.engine.render.shape.ShapeFactory;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteListener;
import ch.logixisland.anuto.engine.sound.SoundFactory;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.math.vector.Vector2;

public abstract class Entity implements SpriteListener, TickListener {

    public static Predicate<Entity> inRange(final Vector2 center, final float range) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity value) {
                return value.getDistanceTo(center) <= range;
            }
        };
    }

    public static Predicate<Entity> onLine(final Vector2 p1, final Vector2 p2, final float lineWidth) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity value) {
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

    public static Function<Entity, Float> distanceTo(final Vector2 toPoint) {
        return new Function<Entity, Float>() {
            @Override
            public Float apply(Entity input) {
                return input.getDistanceTo(toPoint);
            }
        };
    }

    private final Vector2 mPosition = new Vector2();
    private final List<EntityListener> mListeners = new CopyOnWriteArrayList<>();

    public abstract int getType();

    public Object initStatic() {
        return null;
    }

    public void init() {

    }

    public void clean() {
        for (EntityListener l : mListeners) {
            l.entityRemoved(this);
        }
    }

    public void remove() {
        getGameEngine().remove(this);
    }

    @Override
    public void tick() {

    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        canvas.translate(mPosition.x, mPosition.y);
    }


    protected GameEngine getGameEngine() {
        return AnutoApplication.getInstance().getGameFactory().getGameEngine();
    }

    protected LevelDescriptor getLevelDescriptor() {
        return AnutoApplication.getInstance().getGameFactory().getLevelLoader().getLevelDescriptor();
    }

    protected GameSettings getGameSettings() {
        return AnutoApplication.getInstance().getGameFactory().getLevelLoader().getGameSettings();
    }

    protected Object getStaticData() {
        return getGameEngine().getStaticData(this);
    }

    protected SpriteFactory getSpriteFactory() {
        return AnutoApplication.getInstance().getGameFactory().getSpriteFactory();
    }

    protected ShapeFactory getShapeFactory() {
        return AnutoApplication.getInstance().getGameFactory().getShapeFactory();
    }

    protected SoundFactory getSoundFactory() {
        return AnutoApplication.getInstance().getGameFactory().getSoundFactory();
    }


    public Vector2 getPosition() {
        return mPosition;
    }

    public void setPosition(Vector2 position) {
        mPosition.set(position);
    }

    public void move(Vector2 offset) {
        mPosition.add(offset);
    }

    public float getDistanceTo(Entity target) {
        return getDistanceTo(target.mPosition);
    }

    public float getDistanceTo(Vector2 target) {
        return Vector2.fromTo(mPosition, target).len();
    }

    public Vector2 getDirectionTo(Entity target) {
        return getDirectionTo(target.mPosition);
    }

    public Vector2 getDirectionTo(Vector2 target) {
        return Vector2.fromTo(mPosition, target).norm();
    }

    public float getAngleTo(Entity target) {
        return getAngleTo(target.mPosition);
    }

    public float getAngleTo(Vector2 target) {
        return Vector2.fromTo(mPosition, target).angle();
    }

    public boolean isInGame() {
        return mPosition.x >= -0.5f && mPosition.y >= 0.5f &&
                mPosition.x <= getLevelDescriptor().getWidth() + 0.5f &&
                mPosition.y <= getLevelDescriptor().getHeight() + 0.5f;
    }


    public void addListener(EntityListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(EntityListener listener) {
        mListeners.remove(listener);
    }

}
