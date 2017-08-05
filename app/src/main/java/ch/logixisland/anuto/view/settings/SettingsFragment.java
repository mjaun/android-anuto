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
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.SettingsManager;
import ch.logixisland.anuto.business.level.HighScores;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREF_RESET_HIGHSCORES = "reset_highscores";

    private final GameState mGameState;
    private final HighScores mHighScores;
    private final Collection<String> mListPreferenceKeys = new ArrayList<>();

    public SettingsFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameState = factory.getGameState();
        mHighScores = factory.getHighScores();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        registerListPreference(SettingsManager.PREF_BACK_BUTTON_MODE);
        registerListPreference(SettingsManager.PREF_THEME_INDEX);
        setupChangeThemeConfirmationDialog();
        setupResetHighscores();
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
                if (!mGameState.isGameStarted()) {
                    return true;
                }

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(preference.getContext());
                builder.setTitle(R.string.change_theme)
                        .setMessage(R.string.change_theme_warning)
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

    private void setupResetHighscores() {
        Preference preference = findPreference(PREF_RESET_HIGHSCORES);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(preference.getContext());
                builder.setTitle(R.string.reset_highscores)
                        .setMessage(R.string.reset_highscores_warning)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mHighScores.clearHighScores();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.alert)
                        .show();
                return true;
            }
        });
    }
}
