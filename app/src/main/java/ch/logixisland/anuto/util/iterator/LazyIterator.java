package ch.logixisland.anuto.util.iterator;

import java.util.NoSuchElementException;

public abstract class LazyIterator<T> extends StreamIterator<T> {

    private T mNextElement = null;
    private boolean mNextFetched = false;

    protected abstract T fetchNext();

    @Override
    public boolean hasNext() {
        if (!mNextFetched) {
            mNextElement = fetchNext();
            mNextFetched = true;
        }

        return mNextElement != null;
    }

    @Override
    public T next() {
        if (hasNext()) {
            mNextFetched = false;
            return mNextElement;
        } else {
            throw new NoSuchElementException();
        }
    }
}
