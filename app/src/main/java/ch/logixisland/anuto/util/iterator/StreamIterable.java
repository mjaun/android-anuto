package ch.logixisland.anuto.util.iterator;

public interface StreamIterable<T> extends Iterable<T> {
    StreamIterator<T> iterator();
}
