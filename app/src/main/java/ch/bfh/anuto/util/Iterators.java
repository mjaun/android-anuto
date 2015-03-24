package ch.bfh.anuto.util;

import java.util.Comparator;
import java.util.Iterator;

public final class Iterators {
    private Iterators() {}

    public static <T> Iterator<T> filter(Iterator<T> original, Predicate<? super T> filter) {
        return new FilteringIterator<>(original, filter);
    }

    public static <F, T> Iterator<T> transform(Iterator<F> original, Function<? super F, ? extends T> transformation) {
        return new TransformingIterator<>(original, transformation);
    }

    public static <F, T> Iterator<T> cast(Iterator<F> original, final Class<T> castTo) {
        return new TransformingIterator<>(original, new Function<F, T>() {
            @Override
            public T apply(F input) {
                return castTo.cast(input);
            }
        });
    }

    public static <T> T max(Iterator<T> original, Function<T, Float> scoreFunction) {
        T maxObject = null;
        float maxValue = 0f;

        while(original.hasNext()) {
            T object = original.next();
            float value = scoreFunction.apply(object);

            if (maxObject == null || value > maxValue) {
                maxObject = object;
                maxValue = value;
            }
        }

        return maxObject;
    }

    public static <T> T min(Iterator<T> original, Function<T, Float> scoreFunction) {
        T minObject = null;
        float minValue = 0f;

        while(original.hasNext()) {
            T object = original.next();
            float value = scoreFunction.apply(object);

            if (minObject == null || value < minValue) {
                minObject = object;
                minValue = value;
            }
        }

        return minObject;
    }
}
