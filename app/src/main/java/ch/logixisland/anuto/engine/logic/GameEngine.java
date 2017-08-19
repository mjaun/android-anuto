package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityStore;
import ch.logixisland.anuto.engine.logic.loop.GameLoop;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.loop.MessageQueue;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.engine.render.sprite.SpriteFactory;
import ch.logixisland.anuto.engine.sound.SoundFactory;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class GameEngine {

    public final static int TARGET_FRAME_RATE = GameLoop.TARGET_FRAME_RATE;

    private final SpriteFactory mSpriteFactory;
    private final ThemeManager mThemeManager;
    private final SoundFactory mSoundFactory;

    private final EntityStore mEntityStore;
    private final MessageQueue mMessageQueue;
    private final Renderer mRenderer;
    private final GameLoop mGameLoop;

    private GameConfiguration mGameConfiguration;

    public GameEngine(SpriteFactory spriteFactory, ThemeManager themeManager,
                      SoundFactory soundFactory, EntityStore entityStore, MessageQueue messageQueue,
                      Renderer renderer, GameLoop gameLoop) {
        mSpriteFactory = spriteFactory;
        mThemeManager = themeManager;
        mSoundFactory = soundFactory;
        mEntityStore = entityStore;
        mMessageQueue = messageQueue;
        mRenderer = renderer;
        mGameLoop = gameLoop;

        mGameLoop.add(mMessageQueue);
        mGameLoop.add(mEntityStore);
    }

    public SpriteFactory getSpriteFactory() {
        return mSpriteFactory;
    }

    public ThemeManager getThemeManager() {
        return mThemeManager;
    }

    public SoundFactory getSoundFactory() {
        return mSoundFactory;
    }

    public Object getStaticData(Entity entity) {
        return mEntityStore.getStaticData(entity);
    }

    public StreamIterator<Entity> get(int typeId) {
        return mEntityStore.get(typeId);
    }

    public void add(Entity entity) {
        mEntityStore.add(entity);
    }

    public void add(Drawable drawable) {
        mRenderer.add(drawable);
    }

    public void add(TickListener listener) {
        mGameLoop.add(listener);
    }

    public void remove(Entity entity) {
        mEntityStore.remove(entity);
    }

    public void remove(Drawable drawable) {
        mRenderer.remove(drawable);
    }

    public void remove(TickListener listener) {
        mGameLoop.remove(listener);
    }

    public void clear() {
        mMessageQueue.clear();
        mEntityStore.clear();
        mRenderer.clear();
        mGameLoop.clear();

        mGameLoop.add(mMessageQueue);
        mGameLoop.add(mEntityStore);
    }

    public void start() {
        mGameLoop.start();
    }

    public void stop() {
        mGameLoop.stop();
    }

    public void post(Message message) {
        mMessageQueue.post(message);
    }

    public void postDelayed(Message message, float delay) {
        mMessageQueue.postDelayed(message, (int) (delay * TARGET_FRAME_RATE));
    }

    public void setTicksPerLoop(int ticksPerLoop) {
        mGameLoop.setTicksPerLoop(ticksPerLoop);
    }

    public boolean isThreadChangeNeeded() {
        return mGameLoop.isThreadChangeNeeded();
    }

    public boolean isPositionVisible(Vector2 position) {
        return mRenderer.isPositionVisible(position);
    }

    public GameConfiguration getGameConfiguration() {
        return mGameConfiguration;
    }

    public void setGameConfiguration(GameConfiguration gameConfiguration) {
        mGameConfiguration = gameConfiguration;
    }
}
