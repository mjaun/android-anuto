package ch.logixisland.anuto.view;

import android.app.Fragment;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.setting.SettingsManager;

public class AnutoFragment extends Fragment {

    private final SettingsManager mSettingsManager;

    public AnutoFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mSettingsManager = factory.getSettingsManager();
    }

    protected void updateMenuTransparency() {
        View view = getView();

        if (view != null) {
            if (mSettingsManager.isTransparentMenusEnabled()) {
                view.setAlpha(0.73f);
            } else {
                view.setAlpha(1.0f);
            }
        }
    }

}
