package ch.logixisland.anuto.view.map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.InputStream;

import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.map.PathDescriptor;
import ch.logixisland.anuto.data.map.PlateauDescriptor;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.util.math.Vector2;

class MapThumbGenerator {

    private static final int PIXELS_PER_SQUARE = 10;

    private static final int PLATEAU_COLOR = Color.parseColor("#bbbbbb");
    private static final int PATH_COLOR = Color.parseColor("#000000");

    Bitmap generateThumb(Resources resources, int mapDescriptorResId) {
        try {
            InputStream inputStream = resources.openRawResource(mapDescriptorResId);
            MapDescriptor mapDescriptor = MapDescriptor.fromXml(inputStream);
            return generateThumb(mapDescriptor);
        } catch (Exception e) {
            return null;
        }
    }

    Bitmap generateThumb(MapDescriptor mapDescriptor) {
        Bitmap bitmap = Bitmap.createBitmap(
                mapDescriptor.getWidth() * PIXELS_PER_SQUARE,
                mapDescriptor.getHeight() * PIXELS_PER_SQUARE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Viewport viewport = new Viewport();
        viewport.setGameSize(mapDescriptor.getWidth(), mapDescriptor.getHeight());
        viewport.setScreenSize(bitmap.getWidth(), bitmap.getHeight());
        canvas.concat(viewport.getScreenMatrix());

        drawPaths(canvas, mapDescriptor);
        drawPlateaus(canvas, mapDescriptor);

        return bitmap;
    }

    private void drawPaths(Canvas canvas, MapDescriptor mapDescriptor) {
        Paint pathPaint = new Paint();
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(PATH_COLOR);

        for (PathDescriptor path : mapDescriptor.getPaths()) {
            Vector2 lastWayPoint = null;
            for (Vector2 wayPoint : path.getWayPoints()) {
                if (lastWayPoint != null) {
                    canvas.drawRect(
                            Math.min(lastWayPoint.x(), wayPoint.x()) - 0.5f,
                            Math.min(lastWayPoint.y(), wayPoint.y()) - 0.5f,
                            Math.max(lastWayPoint.x(), wayPoint.x()) + 0.5f,
                            Math.max(lastWayPoint.y(), wayPoint.y()) + 0.5f,
                            pathPaint);
                }
                lastWayPoint = wayPoint;
            }
        }
    }

    private void drawPlateaus(Canvas canvas, MapDescriptor mapDescriptor) {
        Paint plateauPaint = new Paint();
        plateauPaint.setStyle(Paint.Style.STROKE);
        plateauPaint.setStrokeWidth(1);
        plateauPaint.setColor(PLATEAU_COLOR);

        for (PlateauDescriptor plateau : mapDescriptor.getPlateaus()) {
            Vector2 position = plateau.getPosition();
            canvas.drawPoint(position.x(), position.y(), plateauPaint);
        }
    }

}
