package ch.logixisland.anuto.util.container;

import android.util.SparseArray;

import ch.logixisland.anuto.util.iterator.LazyIterator;
import ch.logixisland.anuto.util.iterator.StreamIterable;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class MultiMap<T> implements StreamIterable<T> {

    /*
    ------ Members ------
     */

    private final SparseArray<SmartCollection<T>> mCollections = new SparseArray<>();

    /*
    ------ SmartIterator Class ------
     */

    private class LayerIterator extends LazyIterator<T> {
        int mKeyIndex = 0;
        StreamIterator<T> mCollectionIterator;

        @Override
        protected T fetchNext() {
            while (mCollectionIterator == null || !mCollectionIterator.hasNext()) {
                if (mKeyIndex < mCollections.size()) {
                    int key = mCollections.keyAt(mKeyIndex++);
                    mCollectionIterator = mCollections.get(key).iterator();
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

    /*
    ------ Methods ------
     */

    public SmartCollection<T> get(int key) {
        SmartCollection<T> collection = mCollections.get(key);

        if (collection == null) {
            collection = new SmartCollection<T>();
            mCollections.put(key, collection);
        }

        return collection;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new LayerIterator();
    }

    public boolean add(int key, T value) {
        return get(key).add(value);
    }

    public boolean remove(int key, T value) {
        return get(key).remove(value);
    }

    public void clear() {
        mCollections.clear();
    }

}
