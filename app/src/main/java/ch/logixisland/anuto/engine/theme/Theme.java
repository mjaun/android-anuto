package ch.logixisland.anuto.engine.theme;

import android.content.Context;
import android.content.res.TypedArray;

import ch.logixisland.anuto.R;

public class Theme {

    private final Context mContext;
    private final int mThemeNameId;
    private final int mThemeStyleId;

    Theme(Context context, int themeNameId, int themeStyleId) {
        mContext = context;
        mThemeNameId = themeNameId;
        mThemeStyleId = themeStyleId;
    }

    public int getActivityThemeId(ActivityType type) {
        int attrId;

        switch (type) {
            case Game:
                attrId = R.attr.gameActivityStyle;
                break;

            case Popup:
                attrId = R.attr.popupActivityStyle;
                break;

            case Normal:
                attrId = R.attr.normalActivityStyle;
                break;

            default:
                throw new RuntimeException("Unknown activity type!");
        }

        return getResourceId(attrId);
    }

    public String getName() {
        return mContext.getResources().getString(mThemeNameId);
    }

    public int getColor(int attrId) {
        TypedArray values = mContext.obtainStyledAttributes(mThemeStyleId, new int[]{attrId});
        int color = values.getColor(0, 0);
        values.recycle();
        return color;
    }

    public int getResourceId(int attrId) {
        TypedArray values = mContext.obtainStyledAttributes(mThemeStyleId, new int[]{attrId});
        int resId = values.getResourceId(0, 0);
        values.recycle();
        return resId;
    }
}
