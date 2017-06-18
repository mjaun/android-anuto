package ch.logixisland.anuto.engine.sound;

import android.media.SoundPool;

public class Sound {

    private final SoundManager mSoundManager;
    private final SoundPool mSoundPool;
    private final int mSoundId;

    private float mVolume = 1f;

    public Sound(SoundManager soundManager, SoundPool soundPool, int soundId) {
        mSoundManager = soundManager;
        mSoundPool = soundPool;
        mSoundId = soundId;
    }

    public void setVolume(float volume) {
        mVolume = volume;
    }

    public void play() {
        if (mSoundManager.isSoundEnabled()) {
            mSoundPool.play(mSoundId, mVolume, mVolume, 0, 0, 1);
        }
    }

}
