package ch.logixisland.anuto.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by civyshk on 18/06/17.
 */

public class SettingsManager {

    private static final String PREF_FILE = "settings.prefs";
    private static final String PREF_BACK = "backButtonMode";

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

        String backModeCode = mPreferences.getString(PREF_BACK, BackButtonMode.TWICE.getCode());
        mBackButtonMode = BackButtonMode.modeFromCode(backModeCode);
        if (mBackButtonMode == null) {
            mBackButtonMode = BackButtonMode.DISABLED;
        }
    }

    public BackButtonMode getBackButtonMode() {
        return mBackButtonMode;
    }

    public void setBackButtonMode(BackButtonMode mode) {
        if (mBackButtonMode != mode) {
            mBackButtonMode = mode;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(PREF_BACK, mBackButtonMode.getCode());
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
