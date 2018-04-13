package ch.logixisland.anuto.engine.logic.loop;

import java.util.ArrayList;

import ch.logixisland.anuto.data.GameDescriptor;
import ch.logixisland.anuto.engine.logic.persistence.Persister;

public class MessageQueue implements TickListener, Persister {

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

    @Override
    public synchronized void tick() {
        mTickCount++;

        while (!mQueue.isEmpty() && mTickCount >= mQueue.get(0).mDueTickCount) {
            MessageEntry messageEntry = mQueue.remove(0);
            messageEntry.mMessage.execute();
        }
    }

    @Override
    public void writeDescriptor(GameDescriptor gameDescriptor) {
        gameDescriptor.setTickCount(mTickCount);
    }

    @Override
    public void readDescriptor(GameDescriptor gameDescriptor) {
        mTickCount = gameDescriptor.getTickCount();
    }
}
