package ch.logixisland.anuto.engine.sound;

import android.media.SoundPool;

public class Sound {

    private final SoundManager mSoundManager;
    private final SoundPool mSoundPool;
    private final int mSoundId;

    public Sound(SoundManager soundManager, SoundPool soundPool, int soundId) {
        mSoundManager = soundManager;
        mSoundPool = soundPool;
        mSoundId = soundId;
    }

    public void play() {
        if (mSoundManager.isSoundEnabled()) {
            mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
        }
    }

}
