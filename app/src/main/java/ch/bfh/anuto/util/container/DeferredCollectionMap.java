package ch.bfh.anuto.util.container;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ch.bfh.anuto.util.iterator.ComputingIterator;
import ch.bfh.anuto.util.iterator.StreamIterator;

public class DeferredCollectionMap<K, V> implements Iterable<V> {

    /*
    ------ Entry Class ------
     */

    private static class Entry<K, V> {
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K key;
        public V value;
    }

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

    private final ReadWriteLock mLock = new ReentrantReadWriteLock();

    private final SortedMap<K, Collection<V>> mItems = new TreeMap<>();

    private final Queue<Entry<K, V>> mItemsToAdd = new ArrayDeque<>();
    private final Queue<Entry<K, V>> mItemsToRemove = new ArrayDeque<>();

    /*
    ------ Methods ------
     */

    private Collection<V> getOrPutCollection(K key) {
        if (!mItems.containsKey(key)) {
            mItems.put(key, new ArrayList<V>());
        }

        return mItems.get(key);
    }

    protected void onItemAdded(K key, V value) {

    }

    protected void onItemRemoved(K key, V value) {

    }

    protected void onItemAddDeferred(K key, V value) {

    }

    protected void onItemRemoveDeferred(K key, V value) {

    }


    public void addDeferred(K key, V value) {
        synchronized (mItemsToAdd) {
            mItemsToAdd.add(new Entry<>(key, value));
            onItemAddDeferred(key, value);
        }
    }

    public void removeDeferred(K key, V value) {
        synchronized (mItemsToRemove) {
            mItemsToRemove.add(new Entry<>(key, value));
            onItemRemoveDeferred(key, value);
        }
    }

    public void applyChanges() {
        mLock.writeLock().lock();

        synchronized (mItemsToAdd) {
            while (!mItemsToAdd.isEmpty()) {
                Entry<K, V> e = mItemsToAdd.remove();
                getOrPutCollection(e.key).add(e.value);
                onItemAdded(e.key, e.value);
            }
        }

        synchronized (mItemsToRemove) {
            while (!mItemsToRemove.isEmpty()) {
                Entry<K, V> e = mItemsToRemove.remove();

                if (getOrPutCollection(e.key).remove(e.value)) {
                    onItemRemoved(e.key, e.value);
                }
            }
        }

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
