package ch.bfh.anuto.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class ComputingIterator<T> implements Iterator<T> {

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
        }

        if (mNext == null) {
            throw new NoSuchElementException();
        }

        mNextComputed = false;

        return mNext;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
