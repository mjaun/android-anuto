package ch.logixisland.anuto.business.level;

public interface GameSpeedListener {

    void gameSpeedChangedTo(int newSpeed, boolean canIncrease, boolean canDecrease);

}
