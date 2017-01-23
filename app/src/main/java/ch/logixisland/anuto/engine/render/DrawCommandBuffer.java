package ch.logixisland.anuto.engine.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrawCommandBuffer implements Iterable<DrawCommand> {

    private List<DrawCommand> mCommandList = new ArrayList<>();

    public void clear() {
        mCommandList.clear();
    }

    @Override
    public Iterator<DrawCommand> iterator() {
        return mCommandList.iterator();
    }

    public void save() {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.save();
            }
        });
    }

    public void restore() {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.restore();
            }
        });
    }

    public void translate(final float dx, final float dy) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.translate(dx, dy);
            }
        });
    }

    public void scale(final float sx, final float sy) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.scale(sx, sy);
            }
        });
    }

    public void rotate(final float degrees) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.rotate(degrees);
            }
        });
    }

    public void drawBitmap(final Bitmap bitmap, final Matrix matrix, final Paint paint) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.drawBitmap(bitmap, matrix, paint);
            }
        });
    }

    public void drawLine(final float startX, final float startY, final float stopX, final float stopY, final Paint paint) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }
        });
    }

    public void drawRect(final float left, final float top, final float right, final float bottom, final Paint paint) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.drawRect(left, top, right, bottom, paint);
            }
        });
    }

    public void drawCircle(final float cx, final float cy, final float radius, final Paint paint) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.drawCircle(cx, cy, radius, paint);
            }
        });
    }

    public void drawText(final String text, final float x, final float y, final Paint paint) {
        mCommandList.add(new DrawCommand() {
            @Override
            public void execute(Canvas canvas) {
                canvas.drawText(text, x, y, paint);
            }
        });
    }
}
