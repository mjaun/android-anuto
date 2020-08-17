package ch.logixisland.anuto.view.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameLoader;
import ch.logixisland.anuto.business.game.GameSaver;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.SaveGameRepository;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;
import ch.logixisland.anuto.view.load.LoadGameActivity;
import ch.logixisland.anuto.view.map.ChangeMapActivity;
import ch.logixisland.anuto.view.setting.SettingsActivity;
import ch.logixisland.anuto.view.stats.EnemyStatsActivity;

public class MenuActivity extends AnutoActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int REQUEST_CHANGE_MAP = 1;
    private static final int REQUEST_SETTINGS = 2;
    private static final int REQUEST_LOADMENU = 3;
    private static final int REQUEST_ENEMY_STATS = 4;

    private final SaveGameRepository mSaveGameRepository;
    private final GameLoader mGameLoader;
    private final GameSaver mGameSaver;
    private final GameState mGameState;

    private View activity_menu;
    private View menu_layout;

    private Button btn_restart;
    private Button btn_change_map;
    private Button btn_save_game;
    private Button btn_load_game;
    private Button btn_enemy_stats;
    private Button btn_settings;

    public MenuActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mSaveGameRepository = factory.getSaveGameRepository();
        mGameLoader = factory.getGameLoader();
        mGameSaver = factory.getGameSaver();
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

        btn_restart = findViewById(R.id.btn_restart);
        btn_change_map = findViewById(R.id.btn_change_map);
        btn_save_game = findViewById(R.id.btn_save_game);
        btn_load_game = findViewById(R.id.btn_load_game);
        btn_enemy_stats = findViewById(R.id.btn_enemy_stats);
        btn_settings = findViewById(R.id.btn_settings);

        activity_menu = findViewById(R.id.activity_menu);
        menu_layout = findViewById(R.id.menu_layout);

        btn_restart.setOnClickListener(this);
        btn_change_map.setOnClickListener(this);
        btn_save_game.setOnClickListener(this);
        btn_load_game.setOnClickListener(this);
        btn_enemy_stats.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
        btn_save_game.setEnabled(mGameState.isGameStarted());
        btn_load_game.setEnabled(!mSaveGameRepository.getSaveGameInfos().isEmpty());

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

        if (view == btn_save_game) {
            mGameSaver.saveGame();
            btn_load_game.setEnabled(true);
            Toast.makeText(this, getString(R.string.game_saved), Toast.LENGTH_SHORT).show();
            return;
        }

        if (view == btn_load_game) {
            Intent intent = new Intent(this, LoadGameActivity.class);
            startActivityForResult(intent, REQUEST_LOADMENU);
            return;
        }

        if (view == btn_enemy_stats) {
            Intent intent = new Intent(this, EnemyStatsActivity.class);
            startActivityForResult(intent, REQUEST_ENEMY_STATS);
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

        if (requestCode == REQUEST_ENEMY_STATS) {
            finish();
        }

        if (requestCode == REQUEST_SETTINGS) {
            finish();
        }
    }
}
