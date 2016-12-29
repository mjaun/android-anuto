package ch.logixisland.anuto.engine.render.theme;

public class ThemeManager {

    private Theme mTheme;

    public ThemeManager() {
        setTheme(Theme.getDefaultTheme());
    }

    public void setTheme(int dt) {
        setTheme(Theme.getTheme(dt));
    }

    public Theme getTheme() { return mTheme; }

    public void setTheme(Theme theme) {
        mTheme = theme;
    }

}
