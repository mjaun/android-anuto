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

    private final Context mContext;
    private final SharedPreferences mPreferences;

    private Theme mCurrentTheme;
    private List<Theme> mAvailableThemes = new ArrayList<>();
    private List<ThemeListener> mListeners = new CopyOnWriteArrayList<>();

    public ThemeManager(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        initThemes();
        loadTheme();
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
