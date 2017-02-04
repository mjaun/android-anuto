package ch.logixisland.anuto.engine.sound;

import android.content.Context;
import android.content.SharedPreferences;

public class SoundManager {

    private static final String PREF_FILE = "sound.prefs";
    private static final String PREF_SOUND_ENABLED = "enabled";

    private final SharedPreferences mPreferences;

    private boolean mSoundEnabled;

    public SoundManager(Context context) {
        mPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        mSoundEnabled = mPreferences.getBoolean(PREF_SOUND_ENABLED, true);
    }

    public boolean isSoundEnabled() {
        return mSoundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        if (mSoundEnabled != soundEnabled) {
            mSoundEnabled = soundEnabled;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_SOUND_ENABLED, mSoundEnabled);
            editor.apply();
        }
    }
}
