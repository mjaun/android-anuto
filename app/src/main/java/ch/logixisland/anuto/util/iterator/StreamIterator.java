package ch.logixisland.anuto.util.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    public abstract void close();

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


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


    public T min(Function<? super T, Float> scoreFunction) {
        T minObject = null;
        float minValue = 0f;

        while(this.hasNext()) {
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

        while(this.hasNext()) {
            T object = this.next();
            float value = scoreFunction.apply(object);

            if (maxObject == null || value > maxValue) {
                maxObject = object;
                maxValue = value;
            }
        }

        return maxObject;
    }


    public StreamIterator<T> filter(Predicate<? super T> filter) {
        return new FilteringIterator<>(this, filter);
    }

    public <F> StreamIterator<F> transform(Function<? super T, ? extends F> transformation) {
        return new TransformingIterator<>(this, transformation);
    }

    public StreamIterator<T> exclude(final T obj) {
        return new FilteringIterator<>(this, new Predicate<T>() {
            @Override
            public boolean apply(T value) {
                return !value.equals(obj);
            }
        });
    }

    public StreamIterator<T> exclude(final Collection<? extends T> coll) {
        return new FilteringIterator<>(this, new Predicate<T>() {
            @Override
            public boolean apply(T value) {
                return !coll.contains(value);
            }
        });
    }

    public <F> StreamIterator<F> cast(final Class<F> castTo) {
        return new TransformingIterator<>(this, new Function<T, F>() {
            @Override
            public F apply(T input) {
                return castTo.cast(input);
            }
        });
    }

    public <F> StreamIterator<F> ofType(final Class<F> type) {
        Predicate<T> predicate = new Predicate<T>() {
            @Override
            public boolean apply(T value) {
                return type.isInstance(value);
            }
        };

        return this.filter(predicate).cast(type);
    }
}
