package ch.logixisland.anuto.view.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.manager.GameState;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;
import ch.logixisland.anuto.view.level.SelectLevelActivity;
import ch.logixisland.anuto.view.settings.SettingsActivity;

public class MenuActivity extends AnutoActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int REQUEST_SELECT_LEVEL = 1;

    private final GameState mGameState;

    private View activity_menu;
    private View menu_layout;

    private Button btn_restart;
    private Button btn_change_level;
    private Button btn_settings;

    public MenuActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameState = factory.getGameState();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Popup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_restart = (Button) findViewById(R.id.btn_restart);
        btn_change_level = (Button) findViewById(R.id.btn_change_level);
        btn_settings = (Button) findViewById(R.id.btn_settings);

        activity_menu = findViewById(R.id.activity_menu);
        menu_layout = findViewById(R.id.menu_layout);

        btn_restart.setOnClickListener(this);
        btn_change_level.setOnClickListener(this);
        btn_settings.setOnClickListener(this);

        activity_menu.setOnTouchListener(this);
        menu_layout.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_restart) {
            mGameState.restart();
            finish();
        }

        if (view == btn_change_level) {
            Intent intent = new Intent(this, SelectLevelActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_LEVEL);
        }

        if (view == btn_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == menu_layout) {
            return true;
        }

        if (view == activity_menu) {
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_LEVEL) {
            finish();
        }
    }

}
