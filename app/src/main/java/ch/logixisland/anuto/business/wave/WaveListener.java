package ch.logixisland.anuto.business.wave;

public interface WaveListener {
    void waveNumberChanged();
    void nextWaveReadyChanged();
    void remainingEnemiesCountChanged();
}
