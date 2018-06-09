package ch.logixisland.anuto.util.serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Serializer {

    private final Map<Class<?>, Converter<?>> mConverters = new HashMap<>();

    public Serializer() {
        registerPrimitiveConverters();
        registerContainerConverters();
    }

    public <T> void registerConverter(Class<T> type, Converter<T> converter) {
        mConverters.put(type, converter);
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> findConverter(Class<T> type) {
        Converter<T> converter = (Converter<T>) mConverters.get(type);
        if (converter != null) {
            return converter;
        }

        for (Class<?> interfaceType : type.getInterfaces()) {
            converter = (Converter<T>) findConverter(interfaceType);
            if (converter != null) {
                return converter;
            }
        }

        return null;
    }

    public void write(Object value, OutputStream output) throws SerializerException {
        JSONObject json = (JSONObject) write(value);

        try {
            output.write(json.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new SerializerException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object write(Object value) throws SerializerException {
        if (value == null) {
            return null;
        }

        Class type = value.getClass();

        Converter converter = findConverter(type);
        if (converter != null) {
            return converter.write(value);
        }

        if (type.isArray() || type.isPrimitive() || type.isInterface() || type.isAnnotation()) {
            throw new UnsupportedOperationException("Type not supported!");
        }

        if (type.isEnum()) {
            return writeEnum(value);
        }

        return writeObject(value);
    }

    private Object writeEnum(Object value) {
        return value.toString();
    }

    private Object writeObject(Object value) throws SerializerException {
        try {
            JSONObject json = new JSONObject();
            Class<?> currentType = value.getClass();

            while (currentType != Object.class) {
                for (Field field : currentType.getDeclaredFields()) {
                    if (Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }

                    field.setAccessible(true);
                    json.put(field.getName(), write(field.get(value)));
                }

                currentType = currentType.getSuperclass();
            }

            return json;
        } catch (IllegalAccessException e) {
            throw new SerializerException(e);
        } catch (JSONException e) {
            throw new SerializerException(e);
        }
    }

    public <T> T read(Class<T> type, InputStream input) throws SerializerException {
        try {
            char[] buffer = new char[1024];
            StringBuilder stringBuilder = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
            while (true) {
                int count = reader.read(buffer, 0, buffer.length);
                if (count < 0)
                    break;
                stringBuilder.append(buffer, 0, count);
            }
            return read(type, new JSONObject(stringBuilder.toString()));
        } catch (IOException e) {
            throw new SerializerException(e);
        } catch (JSONException e) {
            throw new SerializerException(e);
        }
    }

    private <T> T read(Class<T> type, Object json) throws SerializerException {
        if (json == null) {
            return null;
        }

        Converter<T> converter = findConverter(type);
        if (converter != null) {
            return converter.read(type, json);
        }

        if (type.isArray() || type.isPrimitive() || type.isInterface() || type.isAnnotation()) {
            throw new UnsupportedOperationException("Type not supported!");
        }

        if (type.isEnum()) {
            return readEnum(type, json);
        }

        return readObject(type, json);
    }

    private <T> T readEnum(Class<T> type, Object json) throws SerializerException {
        String jsonString = (String) json;
        for (T value : type.getEnumConstants()) {
            if (value.toString().equals(jsonString)) {
                return value;
            }
        }
        throw new SerializerException("Reading enum failed!");
    }

    private <T> T readObject(Class<T> type, Object json) throws SerializerException {
        try {
            JSONObject jsonObject = (JSONObject) json;

            T instance = type.newInstance();
            Class<?> currentType = type;

            while (currentType != Object.class) {
                for (Field field : currentType.getDeclaredFields()) {
                    if (Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }

                    Object jsonValue = jsonObject.get(field.getName());
                    Object defaultValue = field.get(instance);
                    field.set(instance, read(defaultValue.getClass(), jsonValue));
                }

                currentType = currentType.getSuperclass();
            }

            return instance;
        } catch (ClassCastException e) {
            throw new SerializerException(e);
        } catch (IllegalAccessException e) {
            throw new SerializerException(e);
        } catch (InstantiationException e) {
            throw new SerializerException(e);
        } catch (JSONException e) {
            throw new SerializerException(e);
        }
    }

    private void registerPrimitiveConverters() {
        registerConverter(Integer.class, new Converter<Integer>() {
            @Override
            public Object write(Integer value) {
                return value;
            }

            @Override
            public Integer read(Type type, Object json) throws SerializerException {
                try {
                    return (Integer) json;
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                }
            }
        });
        registerConverter(Long.class, new Converter<Long>() {
            @Override
            public Object write(Long value) {
                return value;
            }

            @Override
            public Long read(Type type, Object json) throws SerializerException {
                try {
                    return (Long) json;
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                }
            }
        });
        registerConverter(Float.class, new Converter<Float>() {
            @Override
            public Object write(Float value) {
                return value;
            }

            @Override
            public Float read(Type type, Object json) throws SerializerException {
                try {
                    return (Float) json;
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                }
            }
        });
        registerConverter(Double.class, new Converter<Double>() {
            @Override
            public Object write(Double value) {
                return value;
            }

            @Override
            public Double read(Type type, Object json) throws SerializerException {
                try {
                    return (Double) json;
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                }
            }
        });
        registerConverter(String.class, new Converter<String>() {
            @Override
            public Object write(String value) {
                return value;
            }

            @Override
            public String read(Type type, Object json) throws SerializerException {
                try {
                    return (String) json;
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                }
            }
        });
        registerConverter(Boolean.class, new Converter<Boolean>() {
            @Override
            public Object write(Boolean value) {
                return value;
            }

            @Override
            public Boolean read(Type type, Object json) throws SerializerException {
                try {
                    return (Boolean) json;
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                }
            }
        });
    }

    private void registerContainerConverters() {
        registerConverter(Collection.class, new Converter<Collection>() {
            @Override
            public Object write(Collection value) throws SerializerException {
                JSONArray json = new JSONArray();
                for (Object item : value) {
                    json.put(Serializer.this.write(item));
                }
                return json;
            }

            @Override
            @SuppressWarnings("unchecked")
            public Collection read(Type type, Object json) throws SerializerException {
                try {
                    Collection result = (Collection) ((Class) type).newInstance();
                    Class<?> itemType = (Class) (((ParameterizedType) type).getActualTypeArguments()[0]);
                    JSONArray array = (JSONArray) json;
                    for (int i = 0; i < array.length(); i++) {
                        result.add(Serializer.this.read(itemType, array.get(i)));
                    }
                    return result;
                } catch (InstantiationException e) {
                    throw new SerializerException(e);
                } catch (IllegalAccessException e) {
                    throw new SerializerException(e);
                } catch (ClassCastException e) {
                    throw new SerializerException(e);
                } catch (JSONException e) {
                    throw new SerializerException(e);
                }
            }
        });
    }
}
