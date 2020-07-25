package ch.logixisland.anuto.view.game;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameLoader;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;

public class GameActivity extends AnutoActivity {

    private final GameLoader mGameLoader;
    private final GameEngine mGameEngine;
    private final TowerSelector mTowerSelector;
    private final BackButtonControl mBackButtonControl;

    private Toast mBackButtonToast;

    private GameView view_tower_defense;

    public GameActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameLoader = factory.getGameLoader();
        mGameEngine = factory.getGameEngine();
        mTowerSelector = factory.getTowerSelector();
        mBackButtonControl = new BackButtonControl(AnutoApplication.getInstance());
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mGameLoader.loadGame();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view_tower_defense = findViewById(R.id.view_tower_defense);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGameEngine.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameLoader.saveGame();
        mGameEngine.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view_tower_defense.close();

        if (mBackButtonToast != null) {
            mBackButtonToast.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mTowerSelector.isTowerSelected()) {
                mTowerSelector.selectTower(null);
                return true;
            } else {
                switch (mBackButtonControl.backButtonPressed()) {
                    case DO_NOTHING:
                        return true;
                    case SHOW_TOAST:
                        mBackButtonToast = showBackButtonToast();
                        return true;
                    default:
                        return super.onKeyDown(keyCode, event);
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private Toast showBackButtonToast() {
        String message = getString(R.string.press_back_button_again_toast);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }
}
