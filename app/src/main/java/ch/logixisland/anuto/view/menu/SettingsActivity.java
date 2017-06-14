package ch.logixisland.anuto.view.menu;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ToggleButton;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.engine.sound.SoundManager;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.view.AnutoActivity;
import ch.logixisland.anuto.view.game.GameActivity;

public class SettingsActivity extends AnutoActivity implements View.OnClickListener, View.OnTouchListener {

    private final GameManager mGameManager;
    private final WaveManager mWaveManager;
    private final ThemeManager mThemeManager;
    private final SoundManager mSoundManager;

    private Handler mHandler;

    private View settings_menu;
    private View menu_layout;

    private Spinner spn_theme;
    private CheckBox cbox_sound;
    private CheckBox cbox_transparent_info;
    private ToggleButton tggl_back_disabled;
    private ToggleButton tggl_back_enabled;
    private ToggleButton tggl_back_twice;

    private final GameListener mGameListener = new GameListener() {
        @Override
        public void gameStarted() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateEnabledStates();
                }
            });
        }

        @Override
        public void gameOver() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateEnabledStates();
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
                    updateEnabledStates();
                }
            });
        }

        @Override
        public void waveFinished() {

        }
    };

    public SettingsActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mThemeManager = factory.getThemeManager();
        mWaveManager = factory.getWaveManager();
        mSoundManager = factory.getSoundManager();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spn_theme = (Spinner) findViewById(R.id.spn_theme);
        cbox_sound = (CheckBox) findViewById(R.id.cbox_sound);
        cbox_transparent_info = (CheckBox) findViewById(R.id.cbox_transparent_info);
        tggl_back_disabled = (ToggleButton) findViewById(R.id.tggl_back_disabled);
        tggl_back_enabled = (ToggleButton) findViewById(R.id.tggl_back_enabled);
        tggl_back_twice = (ToggleButton) findViewById(R.id.tggl_back_twice);

        settings_menu = findViewById(R.id.settings_menu);
        menu_layout = findViewById(R.id.menu_layout);

        spn_theme.setAdapter(mThemeManager);
        spn_theme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mThemeManager.setTheme((int) id);
                mGameManager.restart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cbox_sound.setOnClickListener(this);
        cbox_transparent_info.setOnClickListener(this);
        tggl_back_disabled.setOnClickListener(this);
        tggl_back_enabled.setOnClickListener(this);
        tggl_back_twice.setOnClickListener(this);

        settings_menu.setOnTouchListener(this);
        menu_layout.setOnTouchListener(this);

        mHandler = new Handler();

        mGameManager.addListener(mGameListener);
        mWaveManager.addListener(mWaveListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateEnabledStates();
        updateCheckedStates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameManager.removeListener(mGameListener);
        mWaveManager.removeListener(mWaveListener);
    }

    @Override
    public void onClick(View view) {

        if (view == cbox_sound) {
            mSoundManager.setSoundEnabled(cbox_sound.isChecked());
        }

        if (view == cbox_transparent_info) {
            mThemeManager.setTransparentTowerInfoEnabled(cbox_transparent_info.isChecked());
        }

        if (view == tggl_back_disabled) {
            if(tggl_back_disabled.isChecked()){
                tggl_back_enabled.setChecked(false);
                tggl_back_twice.setChecked(false);
                mThemeManager.setBackButtonMode(ThemeManager.BackButtonMode.DISABLED);
                GameActivity.showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }else{
                tggl_back_disabled.setChecked(true);
            }
        }else if(view == tggl_back_enabled){
            if(tggl_back_enabled.isChecked()){
                tggl_back_disabled.setChecked(false);
                tggl_back_twice.setChecked(false);
                mThemeManager.setBackButtonMode(ThemeManager.BackButtonMode.ENABLED);
                GameActivity.showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }else{
                tggl_back_enabled.setChecked(true);
            }
        }else if(view == tggl_back_twice){
            if(tggl_back_twice.isChecked()){
                tggl_back_disabled.setChecked(false);
                tggl_back_enabled.setChecked(false);
                mThemeManager.setBackButtonMode(ThemeManager.BackButtonMode.TWICE);
                GameActivity.showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }else{
                tggl_back_twice.setChecked(true);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == menu_layout) {
            return true;
        }

        if (view == settings_menu) {
            finish();
            return true;
        }

        return false;
    }

    private void updateEnabledStates() {
        spn_theme.setEnabled(mGameManager.isGameOver() || mWaveManager.getWaveNumber() == 0);
    }

    private void updateCheckedStates(){
        spn_theme.setSelection(mThemeManager.getThemeIndex());
        cbox_sound.setChecked(mSoundManager.isSoundEnabled());
        cbox_transparent_info.setChecked(mThemeManager.isTransparentTowerInfoEnabled());
        tggl_back_disabled.setChecked(mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.DISABLED);
        tggl_back_enabled.setChecked(mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.ENABLED);
        tggl_back_twice.setChecked(mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.TWICE);
    }
}
