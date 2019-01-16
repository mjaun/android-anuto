package ch.logixisland.anuto.engine.logic.loop;

public interface ErrorListener {
    void error(Exception e, int loopCount);
}
