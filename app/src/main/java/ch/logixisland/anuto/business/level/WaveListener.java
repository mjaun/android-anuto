package ch.logixisland.anuto.business.level;

public interface WaveListener {
    void nextWaveReady();

    void waveStarted();

    void waveFinished();
}
