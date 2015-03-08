package ch.bfh.anuto.game;

import org.simpleframework.xml.Element;

public abstract class Enemy extends GameObject {
    @Element(name="path", required=false)
    protected Path mPath;

    public void setPath(Path path) {
        mPath = path;
    }

    public Path getPath() {
        return mPath;
    }
}
