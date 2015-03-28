package ch.bfh.anuto.util.container;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConcurrentCollection<T> implements Collection<T> {

    /*
    ------ Iterator ------
     */

    protected class Itr<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            synchronized (ConcurrentCollection.this) {

            }
        }

        @Override
        public T next() {
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /*
    ------ Members ------
     */

    protected List<T> mData = new ArrayList<>();
    protected List<WeakReference<Itr<T>>> mIterators = new ArrayList<>();

    /*
    ------ Internal Methods -------
     */



    /*
    ------ Collection Interface ------
     */

    @Override
    public synchronized boolean add(T object) {
        return false;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends T> collection) {
        return false;
    }

    @Override
    public synchronized void clear() {

    }

    @Override
    public synchronized boolean contains(Object object) {
        return false;
    }

    @Override
    public synchronized boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public synchronized boolean isEmpty() {
        return false;
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return null;
    }

    @Override
    public synchronized boolean remove(Object object) {
        return false;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public synchronized boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public synchronized int size() {
        return 0;
    }

    @Override
    public synchronized Object[] toArray() {
        return new Object[0];
    }

    @Override
    public synchronized <T1> T1[] toArray(T1[] array) {
        return null;
    }
}
