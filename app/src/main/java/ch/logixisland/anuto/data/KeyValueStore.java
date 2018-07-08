package ch.logixisland.anuto.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.util.math.Vector2;

public class KeyValueStore {

    private JSONObject mJsonObject;

    public KeyValueStore() {
        mJsonObject = new JSONObject();
    }

    private KeyValueStore(JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }

    public static KeyValueStore deserialize(InputStream input) {
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

            return new KeyValueStore(new JSONObject(stringBuilder.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void serialize(OutputStream output) {
        try {
            output.write(mJsonObject.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void putString(String key, String value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String key) {
        try {
            return mJsonObject.getString(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void putInt(String key, int value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String key) {
        try {
            return mJsonObject.getInt(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void putFloat(String key, float value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public float getFloat(String key) {
        try {
            return (float) mJsonObject.getDouble(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void putBoolean(String key, boolean value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getBoolean(String key) {
        try {
            return mJsonObject.getBoolean(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void putVector(String key, Vector2 vector) {
        try {
            mJsonObject.put(key + ".x", vector.x());
            mJsonObject.put(key + ".y", vector.y());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2 getVector(String key) {
        try {
            return new Vector2(
                    (float) mJsonObject.getDouble(key + ".x"),
                    (float) mJsonObject.getDouble(key + ".y")
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void putVectorList(String key, List<Vector2> vectors) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Vector2 vector : vectors) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("x", vector.x());
                jsonObject.put("y", vector.y());
                jsonArray.put(jsonObject);
            }

            mJsonObject.put(key, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Vector2> getVectorList(String key) {
        try {
            JSONArray jsonArray = mJsonObject.getJSONArray(key);
            List<Vector2> vectors = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                vectors.add(new Vector2(
                        (float) jsonObject.getDouble("x"),
                        (float) jsonObject.getDouble("y"))
                );
            }

            return vectors;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendStore(String key, KeyValueStore store) {
        try {
            JSONArray jsonArray = mJsonObject.optJSONArray(key);

            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }

            jsonArray.put(store.mJsonObject);
            mJsonObject.put(key, jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<KeyValueStore> getStoreList(String key) {
        try {
            JSONArray jsonArray = mJsonObject.optJSONArray(key);
            List<KeyValueStore> stores = new ArrayList<>();

            if (jsonArray == null) {
                return stores;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                stores.add(new KeyValueStore(jsonArray.getJSONObject(i)));
            }

            return stores;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
