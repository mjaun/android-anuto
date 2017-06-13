package ch.logixisland.anuto.view.game;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.control.TowerSelector;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.view.AnutoActivity;

public class GameActivity extends AnutoActivity {

    private final GameEngine mGameEngine;
    private final TowerSelector mTowerSelector;
    private final ThemeManager mThemeManager;

    private Toast mBackButtonToast;

    private GameView view_tower_defense;

    public GameActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameEngine = factory.getGameEngine();
        mTowerSelector = factory.getTowerSelector();
        mThemeManager = factory.getThemeManager();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view_tower_defense = (GameView) findViewById(R.id.view_tower_defense);
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
        try {
            mBackButtonToast.cancel();
        }catch(NullPointerException e){
            //noop;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mTowerSelector.selectTower(null);
            
            boolean canWeExit = mThemeManager.backButtonPressed();
            if(!canWeExit && mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.TWICE){
                mBackButtonToast = showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }

            return canWeExit ? super.onKeyDown(keyCode, event) : true;
        }

        return super.onKeyDown(keyCode, event);
    }

    static public Toast showBackButtonToast(Context context, ThemeManager.BackButtonMode mode) {
        String message;
        switch(mode){
            default:
            case DISABLED:
                message = context.getString(R.string.back_button_toast_disabled);
                break;
            case ENABLED:
                message = context.getString(R.string.back_button_toast_enabled);
                break;
            case TWICE:
                message = context.getString(R.string.back_button_toast_twice);
                break;
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }
}
