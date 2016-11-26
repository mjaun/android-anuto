package ch.logixisland.anuto.util.container;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.logixisland.anuto.util.iterator.LazyIterator;
import ch.logixisland.anuto.util.iterator.StreamIterable;
import ch.logixisland.anuto.util.iterator.StreamIterator;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SmartIteratorCollection<T> implements Collection<T>, StreamIterable<T> {

    /*
    ------ Members ------
     */

    private final List<T> mItems;
    private final Collection<SmartIterator> mIterators = new ArrayList<>();

    /*
    ------ Constructors ------
     */

    public SmartIteratorCollection() {
        mItems = new ArrayList<>();
    }

    public SmartIteratorCollection(int capacity) {
        mItems = new ArrayList<>(capacity);
    }

    public SmartIteratorCollection(Collection<? extends T> collection) {
        mItems = new ArrayList<>(collection);
    }

    /*
    ------ SmartIterator Class ------
     */

    private class SmartIterator extends LazyIterator<T> {
        private int mNextIndex = 0;

        SmartIterator() {
            mIterators.add(this);
        }

        @Override
        public T fetchNext() {
            T ret = null;

            if (mNextIndex < mItems.size()) {
                ret = mItems.get(mNextIndex++);
            } else {
                close();
            }

            return ret;
        }

        @Override
        public void close() {
            mIterators.remove(this);
        }

        @Override
        public void remove() {
            SmartIteratorCollection.this.remove(mNextIndex - 1);
        }
    }

    /*
    ------ Methods ------
     */

    private T remove(int index) {
        T ret = mItems.remove(index);

        for (SmartIterator it : mIterators) {
            if (it.mNextIndex > index) {
                it.mNextIndex--;
            }
        }

        return ret;
    }

    @Override
    public boolean add(T object) {
        return mItems.add(object);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return mItems.addAll(collection);
    }

    @Override
    public void clear() {
        mItems.clear();

        for (SmartIterator it : mIterators) {
            it.mNextIndex = 0;
        }
    }

    @Override
    public boolean contains(Object object) {
        return mItems.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mItems.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @NonNull
    @Override
    public StreamIterator<T> iterator() {
        return new SmartIterator();
    }

    @Override
    public boolean remove(Object object) {
        //noinspection SuspiciousMethodCalls
        int index = mItems.indexOf(object);

        if (index >= 0) {
            remove(index);
            return true;
        }

        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean ret = false;

        for (Object item : collection) {
            if (remove(item)) {
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean ret = false;

        for (T item : this) {
            if (!collection.contains(item)) {
                remove(item);
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mItems.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] array) {
        //noinspection SuspiciousToArrayCall
        return mItems.toArray(array);
    }
}
