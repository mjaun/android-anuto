package ch.logixisland.anuto.view.menu;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.engine.sound.SoundManager;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.view.AnutoActivity;

public class SettingsActivity extends AnutoActivity implements View.OnClickListener, View.OnTouchListener {

    private final GameManager mGameManager;
    private final WaveManager mWaveManager;
    private final ThemeManager mThemeManager;
    private final SoundManager mSoundManager;

    private Handler mHandler;

    private View settings_menu;
    private View menu_layout;

    private CheckBox cbox_dark_theme;
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

        cbox_dark_theme = (CheckBox) findViewById(R.id.cbox_dark_theme);//TODO make spinner with every possible theme
        cbox_sound = (CheckBox) findViewById(R.id.cbox_sound);
        cbox_transparent_info = (CheckBox) findViewById(R.id.cbox_transparent_info);
        tggl_back_disabled = (ToggleButton) findViewById(R.id.tggl_back_disabled);
        tggl_back_enabled = (ToggleButton) findViewById(R.id.tggl_back_enabled);
        tggl_back_twice = (ToggleButton) findViewById(R.id.tggl_back_twice);

        settings_menu = findViewById(R.id.settings_menu);
        menu_layout = findViewById(R.id.menu_layout);

        cbox_dark_theme.setOnClickListener(this);
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

        if (view == cbox_dark_theme) {
            mThemeManager.setTheme(getNextTheme());
            mGameManager.restart();
        }

        if (view == cbox_sound) {
            mSoundManager.setSoundEnabled(cbox_sound.isChecked());
            updateEnabledStates();
        }

        if (view == cbox_transparent_info) {
            mThemeManager.setTransparentTowerInfoEnabled(cbox_transparent_info.isChecked());
            updateEnabledStates();
        }

        if (view == tggl_back_disabled) {
            if(tggl_back_disabled.isChecked()){
                tggl_back_enabled.setChecked(false);
                tggl_back_twice.setChecked(false);
                mThemeManager.setBackButtonMode(ThemeManager.BackButtonMode.DISABLED);
                updateEnabledStates();
                showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }else{
                tggl_back_disabled.setChecked(true);
            }
        }else if(view == tggl_back_enabled){
            if(tggl_back_enabled.isChecked()){
                tggl_back_disabled.setChecked(false);
                tggl_back_twice.setChecked(false);
                mThemeManager.setBackButtonMode(ThemeManager.BackButtonMode.ENABLED);
                updateEnabledStates();
                showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }else{
                tggl_back_enabled.setChecked(true);
            }
        }else if(view == tggl_back_twice){
            if(tggl_back_twice.isChecked()){
                tggl_back_disabled.setChecked(false);
                tggl_back_enabled.setChecked(false);
                mThemeManager.setBackButtonMode(ThemeManager.BackButtonMode.TWICE);
                updateEnabledStates();
                showBackButtonToast(this, mThemeManager.getBackButtonMode());
            }else{
                tggl_back_twice.setChecked(true);
            }
        }
    }

    static public void showBackButtonToast(Context context, ThemeManager.BackButtonMode mode) {
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

    private Theme getNextTheme() {
        List<Theme> themes = mThemeManager.getAvailableThemes();
        int index = themes.indexOf(mThemeManager.getTheme()) + 1;
        return themes.get(index % themes.size());
    }

    private void updateEnabledStates() {

//        btn_switch_theme.setText(StringUtils.formatSwitchButton(
//                getString(R.string.theme),
//                getString(mThemeManager.getTheme().getThemeNameId())
//        ));
        cbox_dark_theme.setEnabled(mGameManager.isGameOver() || mWaveManager.getWaveNumber() == 0);

//        btn_switch_sound.setText(StringUtils.formatSwitchButton(
//                getString(R.string.sound),
//                StringUtils.formatBoolean(mSoundManager.isSoundEnabled(), getResources()))
//        );

//        btn_switch_back_button.setText(StringUtils.formatSwitchButton(
//                getString(R.string.back_button),
//                StringUtils.formatBoolean(mThemeManager.getBackButtonMode(), getResources()))
//        );

//        btn_switch_transparent_tower_info_button.setText(StringUtils.formatSwitchButton(
//                getString(R.string.transparent_tower_info_button),
//                StringUtils.formatBoolean(mThemeManager.isTransparentTowerInfoEnabled(), getResources()))
//        );
    }

    private void updateCheckedStates(){
        cbox_dark_theme.setChecked(mThemeManager.getTheme().getGameThemeId() == R.style.DarkTheme);
        cbox_sound.setChecked(mSoundManager.isSoundEnabled());
        cbox_transparent_info.setChecked(mThemeManager.isTransparentTowerInfoEnabled());
        tggl_back_disabled.setChecked(mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.DISABLED);
        tggl_back_enabled.setChecked(mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.ENABLED);
        tggl_back_twice.setChecked(mThemeManager.getBackButtonMode() == ThemeManager.BackButtonMode.TWICE);
    }
}
