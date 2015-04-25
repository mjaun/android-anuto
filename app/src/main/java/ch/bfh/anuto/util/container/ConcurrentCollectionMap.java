package ch.bfh.anuto.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ch.bfh.anuto.util.iterator.ComputingIterator;
import ch.bfh.anuto.util.iterator.StreamIterator;

public class ConcurrentCollectionMap<K, V> implements Iterable<V> {

    /*
    ------ Members ------
     */

    private final Map<K, ConcurrentCollection<V>> mItems = new TreeMap<>();
    private final List<K> mKeys = new ArrayList<>();
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();

    private final Collection<Itr> mIterators = new ArrayList<>();

    /*
    ------ Iterator Class ------
     */

    private class Itr extends ComputingIterator<V> {
        int mKeyIndex = 0;
        StreamIterator<V> mCollectionIterator;

        public Itr() {
            synchronized (mIterators) {
                mIterators.add(this);
            }
        }

        @Override
        protected V computeNext() {
            while (mCollectionIterator == null || !mCollectionIterator.hasNext()) {
                mLock.readLock().lock();

                if (mKeyIndex < mKeys.size()) {
                    K key = mKeys.get(mKeyIndex++);
                    mCollectionIterator = mItems.get(key).iterator();
                } else {
                    mCollectionIterator = null;
                }

                mLock.readLock().unlock();

                if (mCollectionIterator == null) {
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

            synchronized (mIterators) {
                mIterators.remove(this);
            }
        }
    }

    /*
    ------ Methods ------
     */

    protected Collection<V> getCollection(K key) {
        mLock.writeLock().lock();
        if (!mItems.containsKey(key)) {
            mItems.put(key, new ConcurrentCollection<V>());
            mKeys.add(key);
        }

        Collection<V> ret = mItems.get(key);
        mLock.writeLock().unlock();
        return ret;
    }

    public boolean add(K key, V value) {
        return getCollection(key).add(value);
    }

    public boolean remove(K key, V value) {
        return getCollection(key).remove(value);
    }

    public void clear() {
        mLock.writeLock().lock();

        for (K key : mKeys) {
            mItems.get(key).clear();
        }

        mItems.clear();

        mLock.writeLock().unlock();
    }

    @Override
    public StreamIterator<V> iterator() {
        return new Itr();
    }

    public StreamIterator<V> iteratorKey(K key) {
        return (StreamIterator<V>)getCollection(key).iterator();
    }
}
