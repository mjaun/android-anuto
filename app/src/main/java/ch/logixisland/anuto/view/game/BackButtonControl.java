package ch.logixisland.anuto.view.game;

import ch.logixisland.anuto.business.manager.BackButtonMode;
import ch.logixisland.anuto.business.manager.SettingsManager;

public class BackButtonControl {

    private static final long BACK_TWICE_INTERVAL = 2000; //ms

    private final SettingsManager mSettingsManager;

    private long mLastBackButtonPress;

    public BackButtonControl(SettingsManager settingsManager) {
        mSettingsManager = settingsManager;
    }

    public BackButtonMode getBackButtonMode() {
        return mSettingsManager.getBackButtonMode();
    }

    /**
     * Tell the SettingsManager that user wants to exit with back button. Depending on the current
     * BackButtonMode, the SettingsManager will allow to exit or not.
     *
     * @return true if BackButtonMode is ENABLED or if it is TWICE and this is the second press.
     * false if BackButtonMode is DISABLED or if it is TWICE and this is the first press.
     */
    boolean backButtonPressed() {
        long timeNow = System.currentTimeMillis();

        switch (mSettingsManager.getBackButtonMode()) {
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
