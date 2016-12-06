package ch.logixisland.anuto.game.engine;

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

        while (index >= 0 && mQueue.get(index).mDueTickCount > dueTickCount) {
            index--;
        }

        mQueue.add(index, new Message(runnable, dueTickCount));
    }

    synchronized void clear() {
        mQueue.clear();
    }

    synchronized void tick() {
        mTickCount++;

        while (!mQueue.isEmpty() && mQueue.get(0).mDueTickCount <= mTickCount) {
            Message message = mQueue.remove(0);
            message.mRunnable.run();
        }
    }

}
