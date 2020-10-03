package ch.logixisland.anuto.util.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public abstract class StreamIterator<T> implements Iterator<T> {

    public static <T> StreamIterator<T> fromIterator(final Iterator<T> it) {
        return new StreamIterator<T>() {
            @Override
            public void close() {
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }
        };
    }

    public static <T> StreamIterator<T> fromIterable(final Iterable<T> it) {
        return fromIterator(it.iterator());
    }

    public static <T> StreamIterator<T> fromArray(final T[] array) {
        return new StreamIterator<T>() {
            int mIndex = 0;

            @Override
            public void close() {

            }

            @Override
            public boolean hasNext() {
                return mIndex < array.length;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return array[mIndex++];
            }
        };
    }


    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public abstract void close();


    public T first() {
        T first = null;

        if (this.hasNext()) {
            first = this.next();
        }

        this.close();
        return first;
    }

    public T last() {
        T last = null;

        while (this.hasNext()) {
            last = this.next();
        }

        return last;
    }

    public T random(Random random) {
        List<T> list = toList();

        if (list.isEmpty()) {
            return null;
        }

        int index = random.nextInt(list.size());
        return list.get(index);
    }

    public int count() {
        int cnt = 0;

        while (this.hasNext()) {
            this.next();
            cnt++;
        }

        return cnt;
    }

    public boolean isEmpty() {
        boolean empty = !this.hasNext();
        this.close();
        return empty;
    }

    public List<T> toList() {
        List<T> ret = new ArrayList<>();

        while (this.hasNext()) {
            ret.add(this.next());
        }

        return ret;
    }

    public String toString(String delim) {
        StringBuilder sb = new StringBuilder();

        if (this.hasNext()) {
            sb.append(this.next().toString());
        }

        while (this.hasNext()) {
            sb.append(delim);
            sb.append(this.next().toString());
        }

        return sb.toString();
    }


    public T min(Function<? super T, Float> scoreFunction) {
        T minObject = null;
        float minValue = 0f;

        while (this.hasNext()) {
            T object = this.next();
            float value = scoreFunction.apply(object);

            if (minObject == null || value < minValue) {
                minObject = object;
                minValue = value;
            }
        }

        return minObject;
    }

    public T max(Function<? super T, Float> scoreFunction) {
        T maxObject = null;
        float maxValue = 0f;

        while (this.hasNext()) {
            T object = this.next();
            float value = scoreFunction.apply(object);

            if (maxObject == null || value > maxValue) {
                maxObject = object;
                maxValue = value;
            }
        }

        return maxObject;
    }


    public <F> StreamIterator<F> map(Function<? super T, ? extends F> transformation) {
        return new MappingIterator<>(this, transformation);
    }

    public StreamIterator<T> filter(Predicate<? super T> filter) {
        return new FilteringIterator<>(this, filter);
    }

    public StreamIterator<T> filter(final T object) {
        return new FilteringIterator<>(this, value -> !value.equals(object));
    }

    public StreamIterator<T> filter(final Collection<? extends T> collection) {
        return new FilteringIterator<>(this, value -> !collection.contains(value));
    }

    public <F> StreamIterator<F> filter(final Class<F> klass) {
        return new FilteringIterator<>(this, klass::isInstance).cast(klass);
    }

    public <F> StreamIterator<F> cast(final Class<F> castTo) {
        return new MappingIterator<>(this, castTo::cast);
    }

    public <F> StreamIterator<F> ofType(final Class<F> type) {
        Predicate<T> predicate = type::isInstance;

        return this.filter(predicate).cast(type);
    }
}
