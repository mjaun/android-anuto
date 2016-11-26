package ch.logixisland.anuto.util.iterator;

class FilteringIterator<T> extends LazyIterator<T> {

    private Predicate<? super T> mFilter;
    private StreamIterator<T> mOriginal;

    FilteringIterator(StreamIterator<T> original, Predicate<? super T> filter) {
        mOriginal = original;
        mFilter = filter;
    }

    @Override
    public void close() {
        mOriginal.close();
    }

    @Override
    protected T fetchNext() {
        while (mOriginal.hasNext()) {
            T next = mOriginal.next();

            if (mFilter.apply(next)) {
                return next;
            }
        }

        return null;
    }
}
