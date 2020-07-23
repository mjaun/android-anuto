package ch.logixisland.anuto.view.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameLoader;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;
import ch.logixisland.anuto.view.faq.FAQActivity;
import ch.logixisland.anuto.view.loadmenu.LoadMenuActivity;
import ch.logixisland.anuto.view.map.ChangeMapActivity;
import ch.logixisland.anuto.view.setting.SettingsActivity;

public class MenuActivity extends AnutoActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int REQUEST_CHANGE_MAP = 1;
    private static final int REQUEST_SETTINGS = 2;
    private static final int REQUEST_LOADMENU = 3;
    private static final int REQUEST_FAQ = 3;

    private final GameLoader mGameLoader;
    private final GameState mGameState;

    private View activity_menu;
    private View menu_layout;

    private Button btn_restart;
    private Button btn_change_map;
    private Button btn_quicksave;
    private Button btn_loadmenu;
    private Button btn_faq;
    private Button btn_settings;

    public MenuActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameLoader = factory.getGameLoader();
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
        btn_change_map = (Button) findViewById(R.id.btn_change_map);
        btn_quicksave = (Button) findViewById(R.id.btn_quicksave);
        btn_loadmenu = (Button) findViewById(R.id.btn_loadmenu);
        btn_faq = (Button) findViewById(R.id.btn_faq);
        btn_settings = (Button) findViewById(R.id.btn_settings);

        activity_menu = findViewById(R.id.activity_menu);
        menu_layout = findViewById(R.id.menu_layout);

        btn_restart.setOnClickListener(this);
        btn_change_map.setOnClickListener(this);
        btn_quicksave.setOnClickListener(this);
        btn_loadmenu.setOnClickListener(this);
        btn_faq.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
        btn_quicksave.setEnabled(mGameState.isGameStarted());
        btn_loadmenu.setEnabled(mGameLoader.hasSavegames());

        activity_menu.setOnTouchListener(this);
        menu_layout.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_restart) {
            mGameLoader.restart();
            finish();
            return;
        }

        if (view == btn_change_map) {
            Intent intent = new Intent(this, ChangeMapActivity.class);
            startActivityForResult(intent, REQUEST_CHANGE_MAP);
            return;
        }

        if (view == btn_quicksave) {
            mGameLoader.makeNewSavegame(this);
            btn_loadmenu.setEnabled(true);
            return;
        }

        if(view == btn_loadmenu) {
            Intent intent = new Intent(this, LoadMenuActivity.class);
            startActivityForResult(intent, REQUEST_LOADMENU);
            return;
        }

        if (view == btn_faq) {
            Intent intent = new Intent(this, FAQActivity.class);
            startActivityForResult(intent, REQUEST_FAQ);
            return;
        }

        if (view == btn_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS);
            return;
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

        if (requestCode == REQUEST_CHANGE_MAP) {
            finish();
        }

        if (requestCode == REQUEST_LOADMENU) {
            finish();
        }

        if (requestCode == REQUEST_FAQ) {
            finish();
        }

        if (requestCode == REQUEST_SETTINGS) {
            finish();
        }
    }

}
