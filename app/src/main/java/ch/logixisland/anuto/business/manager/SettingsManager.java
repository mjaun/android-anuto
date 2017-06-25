package ch.logixisland.anuto.business.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private static final String PREF_FILE = "settings.prefs";

    private static final String PREF_THEME_INDEX = "themeIndex";
    private static final String PREF_TRANSPARENT_TOWER_INFO = "transparentTowerInfo";
    private static final String PREF_BACK_BUTTON_MODE = "backButtonMode";

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public SettingsManager(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public int getThemeIndex() {
        return mPreferences.getInt(PREF_THEME_INDEX, 0);
    }

    public void setThemeIndex(int index) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(PREF_THEME_INDEX, index);
        editor.apply();
    }

    public boolean isTransparentTowerInfoEnabled() {
        return mPreferences.getBoolean(PREF_TRANSPARENT_TOWER_INFO, false);
    }

    public void setTransparentTowerInfoEnabled(boolean enabled) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(PREF_TRANSPARENT_TOWER_INFO, enabled);
        editor.apply();
    }

    public BackButtonMode getBackButtonMode() {
        String backModeString = mPreferences.getString(PREF_BACK_BUTTON_MODE, null);
        return BackButtonMode.fromString(backModeString);
    }

    public void setBackButtonMode(BackButtonMode backButtonMode) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREF_BACK_BUTTON_MODE, backButtonMode.toString());
        editor.apply();
    }

}
