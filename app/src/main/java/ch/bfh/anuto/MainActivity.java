package ch.bfh.anuto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.io.InputStream;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.data.Level;

public class MainActivity extends Activity {

    private TowerDefenseView view_tower_defense;
    private InventoryFragment fragment_inventory;
    private StatusFragment fragment_status;

    private GameEngine mGame;
    private Level mLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            mLevel = Level.deserialize(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGame = new GameEngine(getResources());

        view_tower_defense = (TowerDefenseView)findViewById(R.id.view_tower_defense);
        view_tower_defense.setGame(mGame);

        fragment_inventory = (InventoryFragment)getFragmentManager().findFragmentById(R.id.fragment_inventory);
        fragment_inventory.setGame(mGame);

        fragment_status = (StatusFragment)getFragmentManager().findFragmentById(R.id.fragment_status);
        fragment_status.setGame(mGame);

        mGame.getManager().loadLevel(mLevel);
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
