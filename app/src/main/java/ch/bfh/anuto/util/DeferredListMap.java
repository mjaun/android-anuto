package ch.bfh.anuto.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.bfh.anuto.game.GameObject;

public class DeferredListMap<K, V extends RemovedMark> {

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

    private class ListMapAllIterator extends ComputingIterator<V> {
        Iterator<List<V>> mListIterator;
        Iterator<V> mObjectIterator;

        public ListMapAllIterator() {
            mListIterator = mListMap.values().iterator();

            if (mListIterator.hasNext()) {
                mObjectIterator = mListIterator.next().iterator();
            }
        }

        @Override
        public V computeNext() {
            if (mObjectIterator == null) {
                return null;
            }

            while (true) {
                while (!mObjectIterator.hasNext()) {
                    if (mListIterator.hasNext()) {
                        mObjectIterator = mListIterator.next().iterator();
                    } else {
                        return null;
                    }
                }

                V next = mObjectIterator.next();

                if (!next.hasRemovedMark()) {
                    return next;
                }
            }
        }
    }

    private class ListMapKeyIterator extends ComputingIterator<V> {
        Iterator<V> mObjectIterator;

        public ListMapKeyIterator(K key) {
            mObjectIterator = getList(key).iterator();
        }

        @Override
        public V computeNext() {
            while (true) {
                if (!mObjectIterator.hasNext()) {
                    return null;
                }

                V next = mObjectIterator.next();

                if (!next.hasRemovedMark()) {
                    return next;
                }
            }
        }
    }

    /*
    ------ Members ------
     */

    private final SortedMap<K, List<V>> mListMap = new TreeMap<>();

    private final Queue<Entry<K, V>> mObjectsToAdd = new ArrayDeque<>();
    private final Queue<Entry<K, V>> mObjectsToRemove = new ArrayDeque<>();

    /*
    ------ Methods ------
     */

    private List<V> getList(K key) {
        if (!mListMap.containsKey(key)) {
            mListMap.put(key, new ArrayList<V>());
        }

        return mListMap.get(key);
    }

    public void addDeferred(K key, V value) {
        mObjectsToAdd.add(new Entry<>(key, value));
    }

    public void removeDeferred(K key, V value) {
        value.markAsRemoved();
        mObjectsToRemove.add(new Entry<>(key, value));
    }

    public void applyChanges() {
        while (!mObjectsToAdd.isEmpty()) {
            Entry<K, V> e = mObjectsToAdd.remove();
            getList(e.key).add(e.value);
            e.value.resetRemovedMark();
            onItemAdded(e.key, e.value);
        }

        while (!mObjectsToRemove.isEmpty()) {
            Entry<K, V> e = mObjectsToRemove.remove();

            if (getList(e.key).remove(e.value)) {
                onItemRemoved(e.key, e.value);
            }
        }
    }

    public Iterator<V> getAll() {
        return new ListMapAllIterator();
    }

    public Iterator<V> getByKey(K key) {
        return new ListMapKeyIterator(key);
    }

    protected void onItemAdded(K key, V value) {

    }

    protected void onItemRemoved(K key, V value) {

    }
}
