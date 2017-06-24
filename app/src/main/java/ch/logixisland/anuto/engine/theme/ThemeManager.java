package ch.logixisland.anuto.engine.theme;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;

public class ThemeManager {

    private static final String PREF_FILE = "theme.prefs";
    private static final String PREF_THEME = "themeId";
    private static final String PREF_TRANSPARENT_TOWER_INFO = "transparentTowerInfoEnabled";

    public final Context mContext;
    private final SharedPreferences mPreferences;

    private Theme mTheme;
    private List<Theme> mAvailableThemes = new ArrayList<>();
    private List<ThemeListener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mTransparentTowerInfoEnabled;

    public ThemeManager(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        initThemes();
        loadTheme();

        mTransparentTowerInfoEnabled = mPreferences.getBoolean(PREF_TRANSPARENT_TOWER_INFO, false);
    }

    private void loadTheme() {
        int index = mPreferences.getInt(PREF_THEME, R.style.OriginalTheme);

        if (index < 0 || index > mAvailableThemes.size() - 1) {
            index = 0;
        }

        mTheme = mAvailableThemes.get(index);
    }

    private void initThemes() {
        mAvailableThemes.add(new Theme(mContext, R.string.theme_original, R.style.OriginalTheme, R.style.OriginalTheme_Menu, R.style.OriginalThemeNormal));
        mAvailableThemes.add(new Theme(mContext, R.string.theme_dark, R.style.DarkTheme, R.style.DarkTheme_Menu, R.style.DarkThemeNormal));
    }

    public Theme getTheme() {
        return mTheme;
    }

    public int getThemeIndex(){
        return mAvailableThemes.indexOf(mTheme);
    }

    public void setTheme(Theme theme) {
        if (mTheme != theme) {
            mTheme = theme;

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

    public boolean isTransparentTowerInfoEnabled() {
        return mTransparentTowerInfoEnabled;
    }

    public void setTransparentTowerInfoEnabled(boolean enabled) {
        if (mTransparentTowerInfoEnabled != enabled) {
            mTransparentTowerInfoEnabled = enabled;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_TRANSPARENT_TOWER_INFO, mTransparentTowerInfoEnabled);
            editor.apply();

            for (ThemeListener listener : mListeners) {
                listener.themeSettingsChanged();
            }
        }
    }

    public void addListener(ThemeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ThemeListener listener) {
        mListeners.remove(listener);
    }

    public List<String> getThemeNames() {
        List<String> names = new ArrayList<>(mAvailableThemes.size());
        for (Theme theme : mAvailableThemes) {
            names.add(theme.getName());
        }
        return names;
    }

}
