package ch.logixisland.anuto.util.serializer;

import java.lang.reflect.Type;

public interface Converter<T> {
    Object write(T value) throws SerializerException;
    T read(Type type, Object json) throws SerializerException;
}
