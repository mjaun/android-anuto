package ch.logixisland.anuto.engine.logic;

import java.util.ArrayList;

class MessageQueue {

    private class Message {
        final Runnable mRunnable;
        final long mDueTickCount;

        Message(Runnable runnable, long dueTickCount) {
            mRunnable = runnable;
            mDueTickCount = dueTickCount;
        }
    }

    private final ArrayList<Message> mQueue = new ArrayList<>();
    private int mTickCount = 0;

    synchronized void post(Runnable runnable, int afterTicks) {
        int index = mQueue.size() - 1;
        long dueTickCount = mTickCount + afterTicks;

        for (int i = 0; i < mQueue.size(); i++) {
            if (dueTickCount < mQueue.get(i).mDueTickCount) {
                mQueue.add(i, new Message(runnable, dueTickCount));
                return;
            }
        }

        mQueue.add(new Message(runnable, dueTickCount));
    }

    synchronized void clear() {
        mQueue.clear();
    }

    synchronized void tick() {
        mTickCount++;

        while (!mQueue.isEmpty() && mTickCount >= mQueue.get(0).mDueTickCount) {
            Message message = mQueue.remove(0);
            message.mRunnable.run();
        }
    }

}
