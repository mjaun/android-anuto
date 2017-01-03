package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.util.data.EnemySettings;
import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;
import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.util.data.TowerSettings;
import ch.logixisland.anuto.view.menu.LevelSelectFragment;

public class MainActivity extends Activity {

    private final GameEngine mGameEngine;
    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;

    private GameView view_tower_defense;

    public MainActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameEngine = factory.getGameEngine();
        mGameManager = factory.getGameManager();
        mLevelLoader = factory.getLevelLoader();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        view_tower_defense = (GameView)findViewById(R.id.view_tower_defense);

        try {
            Persister serializer = new Persister();
            InputStream stream;

            stream = getResources().openRawResource(R.raw.game_settings);
            mLevelLoader.setGameSettings(serializer.read(GameSettings.class, stream));

            stream = getResources().openRawResource(R.raw.tower_settings);
            mLevelLoader.setTowerSettings(serializer.read(TowerSettings.class, stream));

            stream = getResources().openRawResource(R.raw.enemy_settings);
            mLevelLoader.setEnemySettings(serializer.read(EnemySettings.class, stream));

            stream = getResources().openRawResource(R.raw.level_1);
            mLevelLoader.setLevelDescriptor(serializer.read(LevelDescriptor.class, stream));

            mGameManager.restart();
        } catch (Exception e) {
            throw new RuntimeException("Could not load level!", e);
        }
    }

    @Override
    public void onResume() {
        super.onStart();

        mGameEngine.start();
        view_tower_defense.start();
    }

    @Override
    public void onPause() {
        super.onStop();

        view_tower_defense.stop();
        mGameEngine.stop();
    }
}
