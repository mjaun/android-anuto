package ch.bfh.anuto.util.container;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.bfh.anuto.util.iterator.StreamIterator;

public class ListenerList<T> {
    private final List<T> mListeners = new CopyOnWriteArrayList<>();

    public void add(T listener) {
        mListeners.add(listener);
    }

    public void remove(T listener) {
        mListeners.remove(listener);
    }

    public <S> StreamIterator<S> get(final Class<S> type) {
        return StreamIterator.fromIterator(mListeners.iterator())
                .ofType(type);
    }
}
