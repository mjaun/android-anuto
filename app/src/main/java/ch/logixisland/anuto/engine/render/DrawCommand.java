package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;

public interface DrawCommand {
    void execute(Canvas canvas);
}
