package ch.logixisland.anuto.business.game;

public interface WaveListener {
    void waveNumberChanged();
    void nextWaveReadyChanged();
    void remainingEnemiesCountChanged();
}
