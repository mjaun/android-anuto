package ch.bfh.anuto.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConcurrentListMap<K, V extends RemovedMark> {

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

    private abstract class ListMapIterator implements Iterator<V>, Iterable<V> {
        private V mNext = null;
        private boolean mNextComputed = false;

        protected abstract V computeNext();

        @Override
        public boolean hasNext() {
            if (!mNextComputed) {
                mNext = computeNext();
                mNextComputed = true;
            }

            return mNext != null;
        }

        @Override
        public V next() {
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

        @Override
        public Iterator<V> iterator() {
            return this;
        }
    }

    private class ListMapAllIterator extends ListMapIterator {
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

                if (!next.isRemoved()) {
                    return next;
                }
            }
        }
    }

    private class ListMapKeyIterator extends ListMapIterator {
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

                if (!next.isRemoved()) {
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

    public void remove(K key, V value) {
        value.markAsRemoved();
        mObjectsToRemove.add(new Entry<>(key, value));
    }

    public void update() {
        while (!mObjectsToAdd.isEmpty()) {
            Entry<K, V> e = mObjectsToAdd.remove();
            getList(e.key).add(e.value);
        }

        while (!mObjectsToRemove.isEmpty()) {
            Entry<K, V> e = mObjectsToRemove.remove();
            getList(e.key).remove(e.value);
        }
    }

    public Iterable<V> getAll() {
        return new ListMapAllIterator();
    }

    public Iterable<V> getByKey(K key) {
        return new ListMapKeyIterator(key);
    }
}
