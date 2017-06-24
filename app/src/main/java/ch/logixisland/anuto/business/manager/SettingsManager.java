package ch.logixisland.anuto.business.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.theme.ThemeManager;

public class SettingsManager {

    private static final String PREF_FILE = "settings.prefs";

    private static final String PREF_THEME_INDEX = "themeIndex";
    private static final String PREF_TRANSPARENT_TOWER_INFO = "transparentTowerInfo";
    private static final String PREF_BACK_BUTTON_MODE = "backButtonMode";

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public enum BackButtonMode {
        DISABLED("DISABLED"), ENABLED("ENABLED"), TWICE("TWICE");

        private final String code;

        private static final Map<String, BackButtonMode> valuesByCode;

        static {
            valuesByCode = new HashMap<>(values().length);
            for (BackButtonMode mode : values()) {
                valuesByCode.put(mode.code, mode);
            }
        }

        BackButtonMode(String code) {
            this.code = code;
        }
        public static BackButtonMode modeFromCode(String code) {
            return valuesByCode.get(code);
        }

        public String getCode() {
            return code;
        }

    }

    private BackButtonMode mBackButtonMode = BackButtonMode.DISABLED;

    private static final long BACK_TWICE_INTERVAL = 2000L;//ms
    private long mLastBackButtonPress = System.currentTimeMillis() - BACK_TWICE_INTERVAL;

    public SettingsManager(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        String backModeCode = mPreferences.getString(PREF_BACK_BUTTON_MODE, BackButtonMode.TWICE.getCode());
        mBackButtonMode = BackButtonMode.modeFromCode(backModeCode);
        if (mBackButtonMode == null) {
            mBackButtonMode = BackButtonMode.DISABLED;
        }
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
        return mBackButtonMode;
    }
    public void setBackButtonMode(BackButtonMode mode) {
        if (mBackButtonMode != mode) {
            mBackButtonMode = mode;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(PREF_BACK_BUTTON_MODE, mBackButtonMode.getCode());
            editor.apply();
        }
    }

    /** Tell the SettingsManager that user wants to exit with back button. Depending on the current
     * BackButtonMode, the SettingsManager will allow to exit or not.
     *
     * @return true if BackButtonMode is ENABLED or if it is TWICE and this is the second press.
     * false if BackButtonMode is DISABLED or if it is TWICE and this is the first press.
     */
    public boolean backButtonPressed() {
        long timeNow = System.currentTimeMillis();
        if(mBackButtonMode == BackButtonMode.DISABLED){
            return false;
        }else if(mBackButtonMode == BackButtonMode.ENABLED){
            return true;
        }else if(mLastBackButtonPress + BACK_TWICE_INTERVAL > timeNow){
            return true;
        }else{
            mLastBackButtonPress = timeNow;
            return false;
        }
    }

}
