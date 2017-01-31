package ch.logixisland.anuto.view;

import android.app.Activity;
import android.os.Bundle;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;

public abstract class AnutoActivity extends Activity {

    private final ThemeManager mThemeManager;

    private final ThemeListener mThemeListener = new ThemeListener() {
        @Override
        public void themeChanged() {
            recreate();
        }
    };

    public AnutoActivity() {
        mThemeManager = getGameFactory().getThemeManager();
    }

    protected GameFactory getGameFactory() {
        return AnutoApplication.getInstance().getGameFactory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(mThemeManager.getActivityThemeId(getActivityType()));
        super.onCreate(savedInstanceState);
        mThemeManager.addListener(mThemeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThemeManager.removeListener(mThemeListener);
    }

    protected abstract ActivityType getActivityType();

}
