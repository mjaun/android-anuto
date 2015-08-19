package ch.logixisland.anuto;

import android.app.Activity;
import android.os.Bundle;

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
        setContentView(R.layout.activity_main);

        view_tower_defense = (GameView)findViewById(R.id.view_tower_defense);

        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            Level lvl = Level.deserialize(inStream);
            GameManager.getInstance().setLevel(lvl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't load level!");
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
