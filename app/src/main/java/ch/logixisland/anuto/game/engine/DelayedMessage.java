package ch.logixisland.anuto.game.engine;

class DelayedMessage implements Runnable, TickListener {

    final GameEngine mGameEngine;
    final Runnable mMessage;
    final TickTimer mTimer;

    public DelayedMessage(GameEngine gameEngine, Runnable message, float delay) {
        mGameEngine = gameEngine;
        mMessage = message;
        mTimer = TickTimer.createInterval(delay);
    }

    @Override
    public void tick() {
        if (mTimer.tick()) {
            mMessage.run();
            mGameEngine.remove(this);
        }
    }

    @Override
    public void run() {
        mGameEngine.add(this);
    }
}
