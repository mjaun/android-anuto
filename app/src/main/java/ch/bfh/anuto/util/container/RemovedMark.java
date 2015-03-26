package ch.bfh.anuto.util.container;

public interface RemovedMark {
    void resetRemovedMark();
    void markAsRemoved();
    boolean hasRemovedMark();
}
