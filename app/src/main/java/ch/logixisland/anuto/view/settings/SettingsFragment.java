package ch.logixisland.anuto.view.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.Collection;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.manager.SettingsManager;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final GameManager mGameManager;
    private final Collection<String> mListPreferenceKeys = new ArrayList<>();

    public SettingsFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        registerListPreference(SettingsManager.PREF_BACK_BUTTON_MODE);
        registerListPreference(SettingsManager.PREF_THEME_INDEX);
        setupChangeThemeConfirmationDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mListPreferenceKeys.contains(key)) {
            updateListPreferenceSummary(key);
        }
    }

    private void registerListPreference(String key) {
        mListPreferenceKeys.add(key);
        updateListPreferenceSummary(key);
    }

    private void updateListPreferenceSummary(String key) {
        ListPreference preference = (ListPreference) findPreference(key);
        preference.setSummary(preference.getEntry());
    }

    private void setupChangeThemeConfirmationDialog() {
        final ListPreference themePreference = (ListPreference) findPreference(SettingsManager.PREF_THEME_INDEX);
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if (!mGameManager.isGameStarted()) {
                    return true;
                }

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(preference.getContext());
                builder.setTitle(R.string.change_theme)
                        .setMessage(R.string.warning_change_theme)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                themePreference.setValue(newValue.toString());
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.alert)
                        .show();
                return false;
            }
        });
    }
}
