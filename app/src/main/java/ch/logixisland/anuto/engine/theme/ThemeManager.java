package ch.logixisland.anuto.engine.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;

public class ThemeManager extends BaseAdapter {

    private static final String PREF_FILE = "theme.prefs";
    private static final String PREF_THEME = "themeId";
    private static final String PREF_BACK = "backButtonMode";
    private static final String PREF_TRANSPARENT_TOWER_INFO = "transparentTowerInfoEnabled";

    private final Context mContext;
    private final SharedPreferences mPreferences;

    private Theme mCurrentTheme;
    private List<Theme> mAvailableThemes = new ArrayList<>();
    private List<ThemeListener> mListeners = new CopyOnWriteArrayList<>();

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

    private boolean mTransparentTowerInfoEnabled;

    public ThemeManager(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        initThemes();
        loadTheme();

        String backModeCode = mPreferences.getString(PREF_BACK, BackButtonMode.TWICE.getCode());
        mBackButtonMode = BackButtonMode.modeFromCode(backModeCode);
        if (mBackButtonMode == null) {
            mBackButtonMode = BackButtonMode.DISABLED;
        }

        mTransparentTowerInfoEnabled = mPreferences.getBoolean(PREF_TRANSPARENT_TOWER_INFO, false);
    }

    private void loadTheme() {
        int index = mPreferences.getInt(PREF_THEME, R.style.OriginalTheme);

        if (index < 0 || index > mAvailableThemes.size() - 1) {
            index = 0;
        }

        mCurrentTheme = mAvailableThemes.get(index);
    }

    private void initThemes() {
        mAvailableThemes.add(new Theme(R.string.theme_original, R.style.OriginalTheme, R.style.OriginalTheme_Menu));
        mAvailableThemes.add(new Theme(R.string.theme_dark, R.style.DarkTheme, R.style.DarkTheme_Menu));
    }

    public Theme getTheme() {
        return mCurrentTheme;
    }

    public int getThemeIndex(){
        return mAvailableThemes.indexOf(mCurrentTheme);
    }

    public void setTheme(Theme theme) {
        if (mCurrentTheme != theme) {
            mCurrentTheme = theme;

            int index = mAvailableThemes.indexOf(theme);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(PREF_THEME, index);
            editor.apply();

            for (ThemeListener listener : mListeners) {
                listener.themeChanged();
            }
        }
    }

    public void setTheme(int index){
        setTheme(mAvailableThemes.get(index));
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

    public boolean isTransparentTowerInfoEnabled() {
        return mTransparentTowerInfoEnabled;
    }

    public void setTransparentTowerInfoEnabled(boolean enabled) {
        if (mTransparentTowerInfoEnabled != enabled) {
            mTransparentTowerInfoEnabled = enabled;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_TRANSPARENT_TOWER_INFO, mTransparentTowerInfoEnabled);
            editor.apply();
            editor.apply();
        }
    }

    public int getActivityThemeId(ActivityType activityType) {
        return mCurrentTheme.getActivityThemeId(activityType);
    }

    public void addListener(ThemeListener listener) {
        mListeners.add(listener);
    }

    public int getColor(int attrId) {
        TypedArray values = mContext.obtainStyledAttributes(mCurrentTheme.getGameThemeId(), new int[]{attrId});
        int color = values.getColor(0, 0);
        values.recycle();
        return color;
    }

    public int getResourceId(int attrId) {
        TypedArray values = mContext.obtainStyledAttributes(mCurrentTheme.getGameThemeId(), new int[]{attrId});
        int resId = values.getResourceId(0, 0);
        values.recycle();
        return resId;
    }

    public void removeListener(ThemeListener listener) {
        mListeners.remove(listener);
    }

    /** Tell the ThemeManager that user wants to exit with back button. Depending on the current
     * BackButtonMode, the ThemeManager will allow to exit or not.
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

    @Override
    public int getCount() {
        return mAvailableThemes.size();
    }

    @Override
    public Object getItem(int position) {
        return mAvailableThemes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        ((TextView) convertView).setText(((Theme) getItem(position)).getThemeNameId());
        return convertView;
    }
}
