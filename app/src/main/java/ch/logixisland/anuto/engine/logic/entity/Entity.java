package ch.logixisland.anuto.engine.logic.entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.sound.SoundFactory;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.Vector2;

public abstract class Entity {

    public static Predicate<Entity> inRange(final Vector2 center, final float range) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity entity) {
                return entity.getDistanceTo(center) <= range;
            }
        };
    }

    public static Predicate<Entity> onLine(final Vector2 p1, final Vector2 p2, final float lineWidth) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity entity) {
                Vector2 line = p1.to(p2);
                Vector2 toObj = p1.to(entity.mPosition);
                Vector2 proj = toObj.proj(line);

                // check whether object is after line end
                if (proj.len() > line.len()) {
                    return false;
                }

                // check whether object is before line end
                if (!MathUtils.equals(proj.angle(), line.angle(), 1f)) {
                    return false;
                }

                return proj.to(toObj).len() <= lineWidth / 2f;

            }
        };
    }

    public static Predicate<Entity> nameEquals(final String name) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity value) {
                return name.equals(value.getEntityName());
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

    private final GameEngine mGameEngine;
    private final List<EntityListener> mListeners = new CopyOnWriteArrayList<>();

    private int mEntityId;
    private Vector2 mPosition = new Vector2();

    protected Entity(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    void setEntityId(int entityId) {
        mEntityId = entityId;
    }

    public int getEntityId() {
        return mEntityId;
    }

    public abstract int getEntityType();

    public String getEntityName() {
        return null;
    }

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

    public void tick() {

    }

    protected Object getStaticData() {
        return getGameEngine().getStaticData(this);
    }

    public GameEngine getGameEngine() {
        return mGameEngine;
    }

    protected SpriteFactory getSpriteFactory() {
        return getGameEngine().getSpriteFactory();
    }

    protected Theme getTheme() {
        return getGameEngine().getThemeManager().getTheme();
    }

    protected SoundFactory getSoundFactory() {
        return getGameEngine().getSoundFactory();
    }

    public Vector2 getPosition() {
        return mPosition;
    }

    public void setPosition(Vector2 position) {
        mPosition = position;
    }

    public void move(Vector2 offset) {
        mPosition = mPosition.add(offset);
    }

    public float getDistanceTo(Entity target) {
        return getDistanceTo(target.mPosition);
    }

    public float getDistanceTo(Vector2 target) {
        return mPosition.to(target).len();
    }

    public Vector2 getDirectionTo(Entity target) {
        return getDirectionTo(target.mPosition);
    }

    public Vector2 getDirectionTo(Vector2 target) {
        return mPosition.to(target).norm();
    }

    public float getAngleTo(Entity target) {
        return getAngleTo(target.mPosition);
    }

    public float getAngleTo(Vector2 target) {
        return mPosition.to(target).angle();
    }

    public boolean isPositionVisible() {
        return getGameEngine().isPositionVisible(mPosition);
    }

    public void addListener(EntityListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(EntityListener listener) {
        mListeners.remove(listener);
    }

}
