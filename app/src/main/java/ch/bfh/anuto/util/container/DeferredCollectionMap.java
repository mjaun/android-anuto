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

public class DeferredCollectionMap<K, V> extends CollectionMap<K, V> {

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
    ------ Members ------
     */

    private final Queue<Entry<K, V>> mItemsToAdd = new ArrayDeque<>();
    private final Queue<Entry<K, V>> mItemsToRemove = new ArrayDeque<>();

    /*
    ------ Methods ------
     */

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
                getCollection(e.key).add(e.value);
                onItemAdded(e.key, e.value);
            }
        }

        synchronized (mItemsToRemove) {
            while (!mItemsToRemove.isEmpty()) {
                Entry<K, V> e = mItemsToRemove.remove();

                if (getCollection(e.key).remove(e.value)) {
                    onItemRemoved(e.key, e.value);
                }
            }
        }

        mLock.writeLock().unlock();
    }
}
