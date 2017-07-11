package ch.logixisland.anuto.business.level;

public interface GameSpeedListener {

    void gameSpeedChanged(int newSpeed, boolean canIncrease, boolean canDecrease);

}
