package ch.logixisland.anuto.engine.theme;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;

public class ThemeManager {

    private Theme mTheme;
    private List<Theme> mAvailableThemes = new ArrayList<>();
    private List<ThemeListener> mListeners = new CopyOnWriteArrayList<>();

    public ThemeManager(Context context) {
        initThemes(context);
        mTheme = mAvailableThemes.get(0);
    }

    private void initThemes(Context context) {
        mAvailableThemes.add(new Theme(context, R.string.theme_original, R.style.OriginalTheme));
        mAvailableThemes.add(new Theme(context, R.string.theme_dark, R.style.DarkTheme));
    }

    public Theme getTheme() {
        return mTheme;
    }

    public void setTheme(Theme theme) {
        if (mTheme != theme) {
            mTheme = theme;

            for (ThemeListener listener : mListeners) {
                listener.themeChanged(theme);
            }
        }
    }

    public void setThemeIndex(int index) {
        setTheme(mAvailableThemes.get(index));
    }

    public void addListener(ThemeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ThemeListener listener) {
        mListeners.remove(listener);
    }

}
