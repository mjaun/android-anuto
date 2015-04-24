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


    public synchronized void addDeferred(K key, V value) {
        mItemsToAdd.add(new Entry<>(key, value));
        onItemAddDeferred(key, value);
    }

    public synchronized void removeDeferred(K key, V value) {
        mItemsToRemove.add(new Entry<>(key, value));
        onItemRemoveDeferred(key, value);
    }

    public synchronized void clearDeferred() {
        mLock.readLock().lock();

        for (K key : mItems.keySet()) {
            for (V value : mItems.get(key)) {
                removeDeferred(key, value);
            }
        }

        mLock.readLock().unlock();
    }

    public synchronized void applyChanges() {
        mLock.writeLock().lock();

        while (!mItemsToAdd.isEmpty()) {
            Entry<K, V> e = mItemsToAdd.remove();
            getCollection(e.key).add(e.value);
            onItemAdded(e.key, e.value);
        }

        while (!mItemsToRemove.isEmpty()) {
            Entry<K, V> e = mItemsToRemove.remove();
            if (getCollection(e.key).remove(e.value)) {
                onItemRemoved(e.key, e.value);
            }
        }

        mLock.writeLock().unlock();

        this.notifyAll();
    }

    public synchronized void waitForChanges() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
