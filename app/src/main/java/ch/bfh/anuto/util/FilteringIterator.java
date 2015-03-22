package ch.bfh.anuto.util;

import java.util.Iterator;

public class FilteringIterator<T> extends ComputingIterator<T> {

    Predicate<? super T> mFilter;
    Iterator<T> mOriginal;

    public FilteringIterator(Iterator<T> original, Predicate<? super T> filter) {
        mOriginal = original;
        mFilter = filter;
    }

    @Override
    protected T computeNext() {
        while (mOriginal.hasNext()) {
            T next = mOriginal.next();

            if (mFilter.apply(next)) {
                return next;
            }
        }

        return null;
    }
}
