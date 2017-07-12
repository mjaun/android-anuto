package ch.logixisland.anuto.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;

public class MenuActivity extends AnutoActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int REQUEST_SELECT_LEVEL = 1;

    private final GameManager mGameManager;
    private final WaveManager mWaveManager;

    private Handler mHandler;

    private View activity_menu;
    private View menu_layout;

    private Button btn_restart;
    private Button btn_change_level;
    private Button btn_settings;

    private final GameListener mGameListener = new GameListener() {
        @Override
        public void gameStarted() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }

        @Override
        public void gameOver() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
    };

    private final WaveListener mWaveListener = new WaveListener() {
        @Override
        public void nextWaveReady() {

        }

        @Override
        public void waveStarted() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }

        @Override
        public void waveFinished() {

        }
    };

    public MenuActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mWaveManager = factory.getWaveManager();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Menu;
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

        mHandler = new Handler();

        mGameManager.addListener(mGameListener);
        mWaveManager.addListener(mWaveListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameManager.removeListener(mGameListener);
        mWaveManager.removeListener(mWaveListener);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_restart) {
            mGameManager.restart();
            finish();
        }

        if (view == btn_change_level) {
            Intent intent = new Intent(this, SelectLevelGridActivity.class);
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

    private void update() {
        btn_change_level.setEnabled(!mGameManager.isGameStarted());
    }
}
