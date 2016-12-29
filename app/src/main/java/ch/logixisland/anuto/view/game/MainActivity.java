package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import java.io.InputStream;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.GameFactory;
import ch.logixisland.anuto.game.business.manager.GameManager;
import ch.logixisland.anuto.game.data.LevelDescriptor;
import ch.logixisland.anuto.game.render.theme.ThemeManager;
import ch.logixisland.anuto.view.menu.LevelSelectFragment;

public class MainActivity extends Activity {

    private final GameEngine mGameEngine;
    private final GameManager mGameManager;
    private final ThemeManager mThemeManager;

    private GameView view_tower_defense;

    public MainActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameEngine = factory.getGameEngine();
        mGameManager = factory.getGameManager();
        mThemeManager = factory.getThemeManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int themeId = getIntent().getIntExtra("theme", 0);
        mThemeManager.setTheme(themeId);
        setContentView(R.layout.activity_main);

        view_tower_defense = (GameView)findViewById(R.id.view_tower_defense);

        try {
            int levelId = getIntent().getIntExtra(LevelSelectFragment.SELECTED_LEVEL, R.raw.level_1);
            try (InputStream inStream = getResources().openRawResource(levelId)) {
                mGameManager.setLevel(LevelDescriptor.deserialize(inStream));
            }
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
