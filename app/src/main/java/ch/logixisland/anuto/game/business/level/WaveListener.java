package ch.logixisland.anuto.game.business.level;

public interface WaveListener {
    void nextWaveReady();
    void waveStarted();
    void waveFinished();
}
