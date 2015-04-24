package ch.bfh.anuto.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ch.bfh.anuto.util.iterator.ComputingIterator;
import ch.bfh.anuto.util.iterator.StreamIterator;

public class CollectionMap<K, V> implements Iterable<V> {

    /*
    ------ Iterators ------
     */

    private abstract class Itr extends ComputingIterator<V> {
        protected boolean mClosed = false;

        public Itr() {
            mLock.readLock().lock();
        }

        @Override
        public void close() {
            if (!mClosed) {
                mClosed = true;
                mLock.readLock().unlock();
            }
        }
    }

    private class AllItr extends Itr {
        Iterator<Collection<V>> mCollectionIterator;
        Iterator<V> mItemIterator;

        public AllItr() {
            mCollectionIterator = mItems.values().iterator();
        }

        @Override
        public V computeNext() {
            if (mClosed) {
                return null;
            }

            while (mItemIterator == null || !mItemIterator.hasNext()) {
                if (mCollectionIterator.hasNext()) {
                    mItemIterator = mCollectionIterator.next().iterator();
                } else {
                    close();
                    return null;
                }
            }

            return mItemIterator.next();
        }
    }

    private class KeyItr extends Itr {
        Iterator<V> mItemIterator;

        public KeyItr(K key) {
            if (!mItems.containsKey(key)) {
                mItemIterator = null;
            } else {
                mItemIterator = mItems.get(key).iterator();
            }
        }

        @Override
        public V computeNext() {
            if (mClosed) {
                return null;
            }

            if (mItemIterator == null || !mItemIterator.hasNext()) {
                close();
                return null;
            }

            return mItemIterator.next();
        }
    }

    /*
    ------ Members ------
     */

    protected final ReadWriteLock mLock;
    protected final Map<K, Collection<V>> mItems;

    /*
    ------ Constructors ------
     */

    public CollectionMap() {
        mLock = new ReentrantReadWriteLock();
        mItems = createMap();
    }

    /*
    ------ Methods ------
     */

    protected Map<K, Collection<V>> createMap() {
        return new TreeMap<>();
    }

    protected Collection<V> createCollection(K key) {
        return new ArrayList<V>();
    }

    protected Collection<V> getCollection(K key) {
        if (!mItems.containsKey(key)) {
            mItems.put(key, new ArrayList<V>());
        }

        return mItems.get(key);
    }


    protected void onItemAdded(K key, V value) {

    }

    protected void onItemRemoved(K key, V value) {

    }


    public void add(K key, V value) {
        mLock.writeLock().lock();
        getCollection(key).add(value);
        onItemAdded(key, value);
        mLock.writeLock().unlock();
    }

    public void remove(K key, V value) {
        mLock.writeLock().lock();
        if (getCollection(key).remove(key)) {
            onItemRemoved(key, value);
        }
        mLock.writeLock().unlock();
    }

    public void clear() {
        mLock.writeLock().lock();
        for (K key : mItems.keySet()) {
            for (V value : mItems.get(key)) {
                onItemRemoved(key, value);
            }
        }
        mItems.clear();
        mLock.writeLock().unlock();
    }


    @Override
    public StreamIterator<V> iterator() {
        return new AllItr();
    }

    public StreamIterator<V> iteratorKey(K key) {
        return new KeyItr(key);
    }
}
