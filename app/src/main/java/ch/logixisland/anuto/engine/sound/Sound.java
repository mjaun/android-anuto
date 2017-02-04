package ch.logixisland.anuto.engine.sound;

import android.media.SoundPool;

public class Sound {

    private final SoundPool mSoundPool;
    private final int mSoundId;

    Sound(SoundPool soundPool, int soundId) {
        mSoundPool = soundPool;
        mSoundId = soundId;
    }

    public void play() {
        mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
    }

}
