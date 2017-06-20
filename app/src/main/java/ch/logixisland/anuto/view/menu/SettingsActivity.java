package ch.logixisland.anuto.view.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.engine.sound.SoundManager;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.util.SettingsManager;
import ch.logixisland.anuto.view.AnutoActivity;
import ch.logixisland.anuto.view.game.GameActivity;

public class SettingsActivity extends AnutoActivity implements View.OnClickListener {

    private final GameManager mGameManager;
    private final WaveManager mWaveManager;
    private final ThemeManager mThemeManager;
    private final SoundManager mSoundManager;
    private final SettingsManager mSettingsManager;

    private Spinner spn_theme;
    private CheckBox cbox_sound;
    private CheckBox cbox_transparent_info;
    private ToggleButton tggl_back_disabled;
    private ToggleButton tggl_back_enabled;
    private ToggleButton tggl_back_twice;

    public SettingsActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mThemeManager = factory.getThemeManager();
        mWaveManager = factory.getWaveManager();
        mSoundManager = factory.getSoundManager();
        mSettingsManager = factory.getSettingsManager();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Normal;
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

        spn_theme.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, mThemeManager.getThemeNames()));
        spn_theme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int currentThemeIndex = mThemeManager.getThemeIndex();
                if (currentThemeIndex != position) {
                    if (themeChangeRequiresRestart()) {
                        showDialogChangeTheme((int) id);
                    } else {
                        mThemeManager.setTheme((int) id);
                        mGameManager.restart();
                    }
                }

                if (view != null) {
                    ((TextView) view).setTextColor(mThemeManager.getColor(R.attr.textColor));
                }
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateCheckedStates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            if (tggl_back_disabled.isChecked()) {
                tggl_back_enabled.setChecked(false);
                tggl_back_twice.setChecked(false);
                mSettingsManager.setBackButtonMode(SettingsManager.BackButtonMode.DISABLED);
                GameActivity.showBackButtonToast(this, mSettingsManager.getBackButtonMode());
            } else {
                tggl_back_disabled.setChecked(true);
            }
        } else if (view == tggl_back_enabled) {
            if (tggl_back_enabled.isChecked()) {
                tggl_back_disabled.setChecked(false);
                tggl_back_twice.setChecked(false);
                mSettingsManager.setBackButtonMode(SettingsManager.BackButtonMode.ENABLED);
                GameActivity.showBackButtonToast(this, mSettingsManager.getBackButtonMode());
            } else {
                tggl_back_enabled.setChecked(true);
            }
        } else if (view == tggl_back_twice) {
            if (tggl_back_twice.isChecked()) {
                tggl_back_disabled.setChecked(false);
                tggl_back_enabled.setChecked(false);
                mSettingsManager.setBackButtonMode(SettingsManager.BackButtonMode.TWICE);
                GameActivity.showBackButtonToast(this, mSettingsManager.getBackButtonMode());
            } else {
                tggl_back_twice.setChecked(true);
            }
        }
    }

    private void updateCheckedStates() {
        spn_theme.setSelection(mThemeManager.getThemeIndex());
        cbox_sound.setChecked(mSoundManager.isSoundEnabled());
        cbox_transparent_info.setChecked(mThemeManager.isTransparentTowerInfoEnabled());
        tggl_back_disabled.setChecked(mSettingsManager.getBackButtonMode() == SettingsManager.BackButtonMode.DISABLED);
        tggl_back_enabled.setChecked(mSettingsManager.getBackButtonMode() == SettingsManager.BackButtonMode.ENABLED);
        tggl_back_twice.setChecked(mSettingsManager.getBackButtonMode() == SettingsManager.BackButtonMode.TWICE);
    }

    private boolean themeChangeRequiresRestart() {
        return !mGameManager.isGameOver() && mWaveManager.getWaveNumber() != 0;
    }

    private void showDialogChangeTheme(final int themeId) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(R.string.change_theme)
                .setMessage(R.string.warning_change_theme)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mThemeManager.setTheme(themeId);
                        mGameManager.restart();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateCheckedStates();
                    }
                })
                .setIcon(R.drawable.alert)
                .show();
    }
}
