package ch.logixisland.anuto.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.view.AnutoActivity;

public class MenuActivity extends AnutoActivity implements View.OnClickListener {

    private static final int REQUEST_SELECT_LEVEL = 1;

    private final GameManager mGameManager;
    private final WaveManager mWaveManager;
    private final ThemeManager mThemeManager;

    private Handler mHandler;

    private Button btn_restart;
    private Button btn_change_level;
    private Button btn_switch_theme;

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
        mThemeManager = factory.getThemeManager();
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
        btn_switch_theme = (Button) findViewById(R.id.btn_switch_theme);

        btn_restart.setOnClickListener(this);
        btn_change_level.setOnClickListener(this);
        btn_switch_theme.setOnClickListener(this);

        btn_switch_theme.setText(getNextTheme().getThemeNameId());

        mHandler = new Handler();
        mGameManager.addListener(mGameListener);
        mWaveManager.addListener(mWaveListener);
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
            Intent intent = new Intent(this, SelectLevelActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_LEVEL);
        }

        if (view == btn_switch_theme) {
            mThemeManager.setTheme(getNextTheme());
            mGameManager.restart();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_LEVEL) {
            finish();
        }
    }

    private Theme getNextTheme() {
        List<Theme> themes = mThemeManager.getAvailableThemes();
        int index = themes.indexOf(mThemeManager.getTheme()) + 1;
        return themes.get(index % themes.size());
    }

    private void update() {
        btn_switch_theme.setEnabled(mGameManager.isGameOver() || mWaveManager.getWaveNumber() == 0);
    }
}
