package ch.logixisland.anuto.engine.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;

public class ThemeManager {

    private static final String PREF_FILE = "theme.prefs";
    private static final String PREF_THEME = "themeId";
    private static final String PREF_BACK = "backButtonEnabled";
    private static final String PREF_TRANSPARENT_TOWER_INFO = "transparentTowerInfoEnabled";

    private final Context mContext;
    private final SharedPreferences mPreferences;

    private Theme mCurrentTheme;
    private List<Theme> mAvailableThemes = new ArrayList<>();
    private List<ThemeListener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mBackEnabled;
    private boolean mTransparentTowerInfoEnabled;

    public ThemeManager(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        initThemes();
        loadTheme();

        mBackEnabled = mPreferences.getBoolean(PREF_BACK, false);
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

    public List<Theme> getAvailableThemes() {
        return Collections.unmodifiableList(mAvailableThemes);
    }

    public Theme getTheme() {
        return mCurrentTheme;
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

    public boolean isBackEnabled() {
        return mBackEnabled;
    }

    public void setBackEnabled(boolean enabled) {
        if (mBackEnabled != enabled) {
            mBackEnabled = enabled;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_BACK, mBackEnabled);
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
}
