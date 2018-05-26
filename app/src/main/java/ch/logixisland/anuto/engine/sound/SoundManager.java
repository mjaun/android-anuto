package ch.logixisland.anuto.engine.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.logixisland.anuto.Preferences;

public class SoundManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final SharedPreferences mPreferences;

    private boolean mSoundEnabled;

    public SoundManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        updateSoundEnabled();
    }

    public boolean isSoundEnabled() {
        return mSoundEnabled;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Preferences.SOUND_ENABLED.equals(key)) {
            updateSoundEnabled();
        }
    }

    private void updateSoundEnabled() {
        mSoundEnabled = mPreferences.getBoolean(Preferences.SOUND_ENABLED, true);
    }
}
