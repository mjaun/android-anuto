package ch.logixisland.anuto.engine.theme;

import android.content.Context;
import android.content.res.TypedArray;

public class Theme {

    private final Context mContext;
    private final int mThemeNameId;
    private final int mGameThemeId;
    private final int mMenuThemeId;
    private final int mLevelsThemeId;

    Theme(Context context, int themeNameId, int gameThemeId, int menuThemeId, int menuLevelsThemeId) {
        mContext = context;
        mThemeNameId = themeNameId;
        mGameThemeId = gameThemeId;
        mMenuThemeId = menuThemeId;
        mLevelsThemeId = menuLevelsThemeId;
    }

    public int getActivityThemeId(ActivityType type) {
        switch (type) {
            case Game:
                return mGameThemeId;
            case Menu:
                return mMenuThemeId;
            case Normal:
                return mLevelsThemeId;
        }

        throw new RuntimeException("Unknown activity type!");
    }

    public String getName() {
        return mContext.getResources().getString(mThemeNameId);
    }

    public int getColor(int attrId) {
        TypedArray values = mContext.obtainStyledAttributes(mGameThemeId, new int[]{attrId});
        int color = values.getColor(0, 0);
        values.recycle();
        return color;
    }

    public int getResourceId(int attrId) {
        TypedArray values = mContext.obtainStyledAttributes(mGameThemeId, new int[]{attrId});
        int resId = values.getResourceId(0, 0);
        values.recycle();
        return resId;
    }
}
