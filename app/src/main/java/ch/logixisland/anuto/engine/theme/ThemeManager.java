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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.R;

public class ThemeManager extends BaseAdapter {

    private static final String PREF_FILE = "theme.prefs";
    private static final String PREF_THEME = "themeId";
    private static final String PREF_TRANSPARENT_TOWER_INFO = "transparentTowerInfoEnabled";

    private final Context mContext;
    private final SharedPreferences mPreferences;

    private Theme mCurrentTheme;
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

        mCurrentTheme = mAvailableThemes.get(index);
    }

    private void initThemes() {
        mAvailableThemes.add(new Theme(R.string.theme_original, R.style.OriginalTheme, R.style.OriginalTheme_Menu, R.style.OriginalThemeNormal));
        mAvailableThemes.add(new Theme(R.string.theme_dark, R.style.DarkTheme, R.style.DarkTheme_Menu, R.style.DarkThemeNormal));
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

    public int getResourceId(int attrId) {//TODO check calls to this. gametheme?
        TypedArray values = mContext.obtainStyledAttributes(mCurrentTheme.getGameThemeId(), new int[]{attrId});
        int resId = values.getResourceId(0, 0);
        values.recycle();
        return resId;
    }

    public void removeListener(ThemeListener listener) {
        mListeners.remove(listener);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item, parent, false);
        }

        ((TextView) convertView).setText(((Theme) getItem(position)).getThemeNameId());
        ((TextView) convertView).setTextColor(getColor(R.attr.textColor));
        ((TextView) convertView).setBackgroundColor(getColor(R.attr.backgroundSpinnerItem));
//        ((TextView) convertView).setTextColor(mContext.getResources().getColor(getColor(R.attr.textColor)));
        return convertView;
    }
}
