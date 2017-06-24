package ch.logixisland.anuto.engine.theme;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.manager.SettingsManager;

public class ThemeManager {

    private final Context mContext;
    private final SettingsManager mSettingsManager;

    private Theme mTheme;
    private List<Theme> mAvailableThemes = new ArrayList<>();
    private List<ThemeListener> mListeners = new CopyOnWriteArrayList<>();

    public ThemeManager(Context context, SettingsManager settingsManager) {
        mContext = context;
        mSettingsManager = settingsManager;

        initThemes();
        loadTheme();
    }

    private void loadTheme() {
        int index = mSettingsManager.getThemeIndex();

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

    public int getThemeIndex() {
        return mAvailableThemes.indexOf(mTheme);
    }

    public void setTheme(Theme theme) {
        if (mTheme != theme) {
            mTheme = theme;

            int index = mAvailableThemes.indexOf(theme);
            mSettingsManager.setThemeIndex(index);

            for (ThemeListener listener : mListeners) {
                listener.themeChanged();
            }
        }
    }

    public void setTheme(int index) {
        setTheme(mAvailableThemes.get(index));
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
