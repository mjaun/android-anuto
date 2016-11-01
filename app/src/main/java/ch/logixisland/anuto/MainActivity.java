package ch.logixisland.anuto;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import java.io.InputStream;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.data.Level;
import ch.logixisland.anuto.menu.LevelSelectFragment;

public class MainActivity extends Activity {

    GameView view_tower_defense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        GameEngine.getInstance().setResources(getResources());
        GameManager.getInstance().setContext(this);
        setContentView(R.layout.activity_main);

        view_tower_defense = (GameView)findViewById(R.id.view_tower_defense);

        int levelId = getIntent().getIntExtra(LevelSelectFragment.SELECTED_LEVEL, R.raw.level_1);

        try {
            InputStream inStream = getResources().openRawResource(levelId);

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
