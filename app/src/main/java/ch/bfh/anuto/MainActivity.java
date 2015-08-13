package ch.bfh.anuto;

import android.app.Activity;
import android.os.Bundle;

import java.io.InputStream;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.data.Level;

public class MainActivity extends Activity {

    private GameManager mManager;

    private TowerDefenseView view_tower_defense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new GameManager(getResources());
        setContentView(R.layout.activity_main);

        view_tower_defense = (TowerDefenseView)findViewById(R.id.view_tower_defense);
        view_tower_defense.setGame(mManager.getGame());

        restart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view_tower_defense.setGame(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mManager.getGame().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mManager.getGame().stop();
    }

    public GameManager getManager() {
        return mManager;
    }

    public void restart() {
        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            Level lvl = Level.deserialize(inStream);
            mManager.setLevel(lvl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't load level!");
        }
    }
}
