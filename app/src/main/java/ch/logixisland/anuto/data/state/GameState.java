package ch.logixisland.anuto.data.state;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class GameState extends KeyValueStore {

    public GameState() {

    }

    private GameState(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static GameState deserialize(InputStream input) {
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

            return new GameState(new JSONObject(stringBuilder.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void serialize(OutputStream output) {
        try {
            output.write(getJsonObject().toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
