package ch.logixisland.anuto.view.settings;

import android.os.Bundle;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;

public class SettingsActivity extends AnutoActivity {
    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setIcon(R.drawable.settings);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
