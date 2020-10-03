package ch.logixisland.anuto.util.container;

import android.util.SparseArray;

import ch.logixisland.anuto.util.iterator.LazyIterator;
import ch.logixisland.anuto.util.iterator.StreamIterable;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class SafeMultiMap<T> implements StreamIterable<T> {

    private class KeyIterator extends LazyIterator<T> {
        int mKeyIndex = 0;
        StreamIterator<T> mCollectionIterator;

        @Override
        protected T fetchNext() {
            while (mCollectionIterator == null || !mCollectionIterator.hasNext()) {
                if (mKeyIndex < mLayers.size()) {
                    mCollectionIterator = mLayers.valueAt(mKeyIndex++).iterator();
                } else {
                    mCollectionIterator = null;
                }

                if (mCollectionIterator == null) {
                    close();
                    return null;
                }
            }

            return mCollectionIterator.next();
        }

        @Override
        public void close() {
            if (mCollectionIterator != null) {
                mCollectionIterator.close();
                mCollectionIterator = null;
            }
        }
    }

    private final SparseArray<SafeCollection<T>> mLayers = new SparseArray<>();

    @Override
    public StreamIterator<T> iterator() {
        return new KeyIterator();
    }

    public SafeCollection<T> get(int key) {
        SafeCollection<T> collection = mLayers.get(key);

        if (collection == null) {
            collection = new SafeCollection<>();
            mLayers.put(key, collection);
        }

        return collection;
    }

    public boolean add(int key, T value) {
        return get(key).add(value);
    }

    public boolean remove(int key, T value) {
        return get(key).remove(value);
    }

    public void clear() {
        mLayers.clear();
    }

}
