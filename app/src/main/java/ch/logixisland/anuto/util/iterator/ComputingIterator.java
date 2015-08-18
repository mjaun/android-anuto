package ch.logixisland.anuto.util.iterator;

import java.util.NoSuchElementException;

public abstract class ComputingIterator<T> extends StreamIterator<T> {

    private T mNext = null;
    private boolean mNextComputed = false;

    protected abstract T computeNext();

    @Override
    public boolean hasNext() {
        if (!mNextComputed) {
            mNext = computeNext();
            mNextComputed = true;
        }

        return mNext != null;
    }

    @Override
    public T next() {
        if (!mNextComputed) {
            mNext = computeNext();
            mNextComputed = true;
        }

        if (mNext == null) {
            throw new NoSuchElementException();
        }

        mNextComputed = false;

        return mNext;
    }
}
