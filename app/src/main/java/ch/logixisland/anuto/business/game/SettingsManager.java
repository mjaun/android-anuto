package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.sound.SoundManager;
import ch.logixisland.anuto.engine.theme.ThemeManager;

public class SettingsManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_THEME_INDEX = "theme_index";
    public static final String PREF_SOUND_ENABLED = "sound_enabled";
    public static final String PREF_TRANSPARENT_MENUS_ENABLED = "transparent_menus_enabled";
    public static final String PREF_BACK_BUTTON_MODE = "back_button_mode";

    private final SharedPreferences mPreferences;
    private final ThemeManager mThemeManager;
    private final SoundManager mSoundManager;

    public SettingsManager(Context context, ThemeManager themeManager, SoundManager soundManager) {
        PreferenceManager.setDefaultValues(context, R.xml.settings, false);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        mThemeManager = themeManager;
        mSoundManager = soundManager;

        updateThemeIndex();
        updateSoundEnabled();
    }

    public boolean isTransparentMenusEnabled() {
        return mPreferences.getBoolean(PREF_TRANSPARENT_MENUS_ENABLED, false);
    }

    public BackButtonMode getBackButtonMode() {
        String backModeString = mPreferences.getString(PREF_BACK_BUTTON_MODE, null);

        try {
            return BackButtonMode.valueOf(backModeString);
        } catch (Exception e) {
            return BackButtonMode.DISABLED;
        }
    }

    private int getThemeIndex() {
        return Integer.valueOf(mPreferences.getString(PREF_THEME_INDEX, "0"));
    }

    private boolean isSoundEnabled() {
        return mPreferences.getBoolean(PREF_SOUND_ENABLED, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PREF_THEME_INDEX.equals(key)) {
            updateThemeIndex();
        }

        if (PREF_SOUND_ENABLED.equals(key)) {
            updateSoundEnabled();
        }
    }

    private void updateThemeIndex() {
        mThemeManager.setThemeIndex(getThemeIndex());
    }

    private void updateSoundEnabled() {
        mSoundManager.setSoundEnabled(isSoundEnabled());
    }
}
