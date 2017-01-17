package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.manager.GameManager;

public class GameActivity extends Activity {

    private final GameEngine mGameEngine;

    private GameView view_tower_defense;

    public GameActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameEngine = factory.getGameEngine();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        view_tower_defense = (GameView)findViewById(R.id.view_tower_defense);
    }

    @Override
    public void onResume() {
        super.onStart();
        mGameEngine.start();
    }

    @Override
    public void onPause() {
        super.onStop();
        mGameEngine.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view_tower_defense.close();
    }
}
