package ch.logixisland.anuto.util;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GenericFactory<T> {

    final Map<String, Constructor<? extends T>> mConstructorMap = new HashMap<>();

    public GenericFactory(Class<T> baseClass, Class<?>... constructorParameterTypes) {
        Reflections reflections = new Reflections(baseClass.getPackage().getName());

        try {
            for (Class<? extends T> subClass : reflections.getSubTypesOf(baseClass)) {
                Constructor<? extends T> constructor = subClass.getConstructor(constructorParameterTypes);
                mConstructorMap.put(subClass.getSimpleName(), constructor);
            }
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
