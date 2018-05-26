package ch.logixisland.anuto.view;

import android.app.Activity;
import android.os.Bundle;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeManager;

public abstract class AnutoActivity extends Activity implements ThemeManager.Listener {

    private final ThemeManager mThemeManager;

    public AnutoActivity() {
        mThemeManager = getGameFactory().getThemeManager();
    }

    protected abstract ActivityType getActivityType();

    protected GameFactory getGameFactory() {
        return AnutoApplication.getInstance().getGameFactory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(mThemeManager.getTheme().getActivityThemeId(getActivityType()));
        super.onCreate(savedInstanceState);
        mThemeManager.addListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThemeManager.removeListener(this);
    }

    @Override
    public void themeChanged(Theme theme) {
        recreate();
    }

}
