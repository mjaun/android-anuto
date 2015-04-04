package ch.bfh.anuto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.io.InputStream;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.data.Level;

public class MainActivity extends Activity {

    private GameEngine mGame;
    private Level mLevel;

    private TowerDefenseView view_tower_defense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGame = new GameEngine(getResources());
        setContentView(R.layout.activity_main);

        view_tower_defense = (TowerDefenseView)findViewById(R.id.view_tower_defense);
        view_tower_defense.setGame(mGame);

        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            mLevel = Level.deserialize(inStream);
            mGame.getManager().loadLevel(mLevel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't load level!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view_tower_defense.setGame(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGame.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGame.stop();
    }

    public GameEngine getGame() {
        return mGame;
    }
}
