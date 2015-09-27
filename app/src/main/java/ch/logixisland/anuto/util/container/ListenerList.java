package ch.logixisland.anuto.util.container;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.util.iterator.StreamIterator;

public class ListenerList<T> {
    private final List<T> mListeners = new CopyOnWriteArrayList<>();

    public void add(T listener) {
        mListeners.add(listener);
    }

    public void remove(T listener) {
        mListeners.remove(listener);
    }

    public <S> Iterable<S> get(final Class<S> type) {
        return new Iterable<S>() {
            @Override
            public Iterator<S> iterator() {
                return StreamIterator.fromIterable(mListeners).ofType(type);
            }
        };
    }
}
