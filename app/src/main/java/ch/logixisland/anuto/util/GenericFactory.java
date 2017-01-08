package ch.logixisland.anuto.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GenericFactory<T> {

    private final Map<String, Constructor<? extends T>> mConstructorMap = new HashMap<>();
    private final Class<?>[] mConstructorParameterTypes;

    public GenericFactory(Class<?>... constructorParameterTypes) {
        mConstructorParameterTypes = constructorParameterTypes;
    }

    public void registerClass(Class<? extends T> type) {
        try {
            Constructor<? extends T> constructor = type.getConstructor(mConstructorParameterTypes);
            mConstructorMap.put(type.getSimpleName(), constructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public T createInstance(String name, Object... arguments) {
        try {
            return mConstructorMap.get(name).newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
