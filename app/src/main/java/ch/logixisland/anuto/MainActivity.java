package ch.logixisland.anuto;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.data.Level;

public class MainActivity extends Activity {

    GameView view_tower_defense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameEngine.getInstance().setResources(getResources());
        GameManager.getInstance().setContext(this);
        setContentView(R.layout.activity_main);

        view_tower_defense = (GameView)findViewById(R.id.view_tower_defense);

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File levelXml = new File(sdCard, "anuto-level.xml");

            InputStream inStream;

            if (levelXml.exists()) {
                inStream = new FileInputStream(levelXml);
            } else {
                inStream = getResources().openRawResource(R.raw.level);
            }

            try {
                GameManager.getInstance().setLevel(Level.deserialize(inStream));
            } finally {
                inStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not load level!", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        GameEngine.getInstance().start();
        view_tower_defense.start();
    }

    @Override
    public void onStop() {
        super.onStop();

        view_tower_defense.stop();
        GameEngine.getInstance().stop();
    }
}