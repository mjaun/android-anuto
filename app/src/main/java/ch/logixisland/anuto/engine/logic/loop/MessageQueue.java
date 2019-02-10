package ch.logixisland.anuto.engine.logic.loop;

import java.util.ArrayList;

import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class MessageQueue implements Persister {

    private static class MessageEntry {
        private final Message mMessage;
        private final long mDueTickCount;

        MessageEntry(Message message, long dueTickCount) {
            mMessage = message;
            mDueTickCount = dueTickCount;
        }
    }

    private final ArrayList<MessageEntry> mQueue = new ArrayList<>();
    private int mTickCount = 0;

    public int getTickCount() {
        return mTickCount;
    }

    public synchronized void post(Message message) {
        postAfterTicks(message, 0);
    }

    public synchronized void postAfterTicks(Message message, int ticks) {
        long dueTickCount = mTickCount + ticks;

        for (int i = 0; i < mQueue.size(); i++) {
            if (dueTickCount < mQueue.get(i).mDueTickCount) {
                mQueue.add(i, new MessageEntry(message, dueTickCount));
                return;
            }
        }

        mQueue.add(new MessageEntry(message, dueTickCount));
    }

    public synchronized void clear() {
        mQueue.clear();
    }

    public synchronized void tick() {
        mTickCount++;
    }

    public synchronized void processMessages() {
        while (!mQueue.isEmpty() && mTickCount >= mQueue.get(0).mDueTickCount) {
            MessageEntry messageEntry = mQueue.remove(0);
            messageEntry.mMessage.execute();
        }
    }

    @Override
    public void resetState() {
        mTickCount = 0;
    }

    @Override
    public void writeState(KeyValueStore gameState) {
        gameState.putInt("tickCount", mTickCount);
    }

    @Override
    public void readState(KeyValueStore gameState) {
        mTickCount = gameState.getInt("tickCount");
    }
}
