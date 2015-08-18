package ch.logixisland.anuto.util.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ch.logixisland.anuto.util.iterator.ComputingIterator;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class ConcurrentCollection<T> implements Collection<T> {

    /*
    ------ Members ------
     */

    protected final List<T> mItems;
    protected final ReadWriteLock mLock = new ReentrantReadWriteLock();

    private final Collection<Itr> mIterators = new ArrayList<>();

    /*
    ------ Constructors ------
     */

    public ConcurrentCollection() {
        mItems = new ArrayList<>();
    }

    public ConcurrentCollection(int capacity) {
        mItems = new ArrayList<>(capacity);
    }

    public ConcurrentCollection(Collection<? extends T> collection) {
        mItems = new ArrayList<>(collection);
    }

    /*
    ------ Iterator Class ------
     */

    private class Itr extends ComputingIterator<T> {
        private int mIndex = 0;

        public Itr() {
            synchronized (mIterators) {
                mIterators.add(this);
            }
        }

        @Override
        public T computeNext() {
            T ret = null;
            mLock.readLock().lock();

            if (mIndex < mItems.size()) {
                ret = mItems.get(mIndex++);
            } else {
                close();
            }

            mLock.readLock().unlock();
            return ret;
        }

        @Override
        public void close() {
            synchronized (mIterators) {
                mIterators.remove(this);
            }
        }
    }

    /*
    ------ Methods ------
     */

    public Lock writeLock() {
        return mLock.writeLock();
    }

    public Lock readLock() {
        return mLock.readLock();
    }

    private T remove(int index) {
        T ret;

        synchronized (mIterators) {
            ret = mItems.remove(index);

            for (Itr it : mIterators) {
                if (it.mIndex > index) {
                    it.mIndex--;
                }
            }
        }

        return ret;
    }

    /*
    ------ Collection Interface ------
     */

    @Override
    public boolean add(T object) {
        boolean ret;
        mLock.writeLock().lock();
        ret = mItems.add(object);
        mLock.writeLock().unlock();
        return ret;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean ret;
        mLock.writeLock().lock();
        ret = mItems.addAll(collection);
        mLock.writeLock().unlock();
        return ret;
    }

    @Override
    public void clear() {
        mLock.writeLock().lock();
        mItems.clear();

        synchronized (mIterators) {
            for (Itr it : mIterators) {
                it.mIndex = 0;
            }
        }

        mLock.writeLock().unlock();
    }

    @Override
    public boolean contains(Object object) {
        boolean ret;
        mLock.readLock().lock();
        ret = mItems.contains(object);
        mLock.readLock().unlock();
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        boolean ret;
        mLock.readLock().lock();
        ret = mItems.containsAll(collection);
        mLock.readLock().unlock();
        return ret;
    }

    @Override
    public boolean isEmpty() {
        boolean ret;
        mLock.readLock().lock();
        ret = mItems.isEmpty();
        mLock.readLock().unlock();
        return ret;
    }

    @Override
    public StreamIterator<T> iterator() {
        return new Itr();
    }

    @Override
    public boolean remove(Object object) {
        boolean ret = false;
        mLock.writeLock().lock();
        int index = mItems.indexOf(object);

        if (index >= 0) {
            remove(index);
            ret = true;
        }

        mLock.writeLock().unlock();
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean ret = false;
        mLock.writeLock().lock();

        for (Object obj : collection) {
            if (remove(obj)) {
                ret = true;
            }
        }

        mLock.writeLock().unlock();
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean ret = false;
        mLock.writeLock().lock();

        Iterator<T> it = iterator();
        while (it.hasNext()) {
            T obj = it.next();

            if (!collection.contains(obj)) {
                it.remove();
                ret = true;
            }
        }

        mLock.writeLock().unlock();
        return ret;
    }

    @Override
    public int size() {
        int ret;
        mLock.readLock().lock();
        ret = mItems.size();
        mLock.readLock().unlock();
        return ret;
    }

    @Override
    public Object[] toArray() {
        Object[] ret;
        mLock.readLock().lock();
        ret = mItems.toArray();
        mLock.readLock().unlock();
        return ret;
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        T1[] ret;
        mLock.readLock().lock();
        ret = mItems.toArray(array);
        mLock.readLock().unlock();
        return ret;
    }
}
