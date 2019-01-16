package ch.logixisland.anuto.view;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import ch.logixisland.anuto.Preferences;

public class AnutoFragment extends Fragment {

    protected void updateMenuTransparency() {
        View view = getView();

        if (view != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean transparentMenusEnabled = preferences.getBoolean(Preferences.TRANSPARENT_MENUS_ENABLED, false);

            if (transparentMenusEnabled) {
                view.setAlpha(0.73f);
            } else {
                view.setAlpha(1.0f);
            }
        }
    }

}
