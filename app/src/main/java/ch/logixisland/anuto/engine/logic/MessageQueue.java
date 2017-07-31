package ch.logixisland.anuto.engine.logic;

import java.util.ArrayList;

public class MessageQueue implements TickListener {

    private static class Message {
        final Runnable mRunnable;
        final long mDueTickCount;

        Message(Runnable runnable, long dueTickCount) {
            mRunnable = runnable;
            mDueTickCount = dueTickCount;
        }
    }

    private final ArrayList<Message> mQueue = new ArrayList<>();
    private int mTickCount = 0;

    public synchronized void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    public synchronized void postDelayed(Runnable runnable, int afterTicks) {
        long dueTickCount = mTickCount + afterTicks;

        for (int i = 0; i < mQueue.size(); i++) {
            if (dueTickCount < mQueue.get(i).mDueTickCount) {
                mQueue.add(i, new Message(runnable, dueTickCount));
                return;
            }
        }

        mQueue.add(new Message(runnable, dueTickCount));
    }

    public synchronized void clear() {
        mQueue.clear();
    }

    @Override
    public synchronized void tick() {
        mTickCount++;

        while (!mQueue.isEmpty() && mTickCount >= mQueue.get(0).mDueTickCount) {
            Message message = mQueue.remove(0);
            message.mRunnable.run();
        }
    }

}
