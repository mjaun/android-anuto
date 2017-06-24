package ch.logixisland.anuto.view.game;

import ch.logixisland.anuto.business.manager.BackButtonMode;
import ch.logixisland.anuto.business.manager.SettingsManager;

public class BackButtonControl {

    private static final long BACK_TWICE_INTERVAL = 2000; //ms

    private final SettingsManager mSettingsManager;

    private BackButtonMode mBackButtonMode;
    private long mLastBackButtonPress;

    public BackButtonControl(SettingsManager settingsManager) {
        mSettingsManager = settingsManager;
        mBackButtonMode = mSettingsManager.getBackButtonMode();
    }

    public BackButtonMode getBackButtonMode() {
        return mBackButtonMode;
    }

    public void setBackButtonMode(BackButtonMode mode) {
        if (mBackButtonMode != mode) {
            mBackButtonMode = mode;

            mSettingsManager.setBackButtonMode(mBackButtonMode);
        }
    }

    /**
     * Tell the SettingsManager that user wants to exit with back button. Depending on the current
     * BackButtonMode, the SettingsManager will allow to exit or not.
     *
     * @return true if BackButtonMode is ENABLED or if it is TWICE and this is the second press.
     * false if BackButtonMode is DISABLED or if it is TWICE and this is the first press.
     */
    public boolean backButtonPressed() {
        long timeNow = System.currentTimeMillis();

        switch (mBackButtonMode)
        {
            case DISABLED:
                return false;

            case ENABLED:
                return true;

            case TWICE:
                if (timeNow < mLastBackButtonPress + BACK_TWICE_INTERVAL) {
                    return true;
                } else {
                    mLastBackButtonPress = timeNow;
                    return false;
                }

            default:
                return false;
        }
    }
}
