package ch.logixisland.anuto.util.iterator;

public class FilteringIterator<T> extends ComputingIterator<T> {

    Predicate<? super T> mFilter;
    StreamIterator<T> mOriginal;

    public FilteringIterator(StreamIterator<T> original, Predicate<? super T> filter) {
        mOriginal = original;
        mFilter = filter;
    }

    @Override
    public void close() {
        mOriginal.close();
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
