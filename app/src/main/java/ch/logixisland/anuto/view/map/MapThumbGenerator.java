package ch.logixisland.anuto.view.map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.engine.logic.map.GameMap;
import ch.logixisland.anuto.engine.logic.map.MapPath;
import ch.logixisland.anuto.engine.logic.map.PlateauInfo;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.util.container.KeyValueStore;
import ch.logixisland.anuto.util.math.Vector2;

class MapThumbGenerator {

    private static final int PIXELS_PER_SQUARE = 10;

    private static final int PLATEAU_COLOR = Color.parseColor("#bbbbbb");
    private static final int PATH_COLOR = Color.parseColor("#000000");

    Bitmap generateThumb(Resources resources, int mapResId) {
        return generateThumb(new GameMap(KeyValueStore.fromResources(resources, mapResId)));
    }

    private Bitmap generateThumb(GameMap map) {
        Bitmap bitmap = Bitmap.createBitmap(
                map.getWidth() * PIXELS_PER_SQUARE,
                map.getHeight() * PIXELS_PER_SQUARE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Viewport viewport = new Viewport();
        viewport.setGameSize(map.getWidth(), map.getHeight());
        viewport.setScreenSize(bitmap.getWidth(), bitmap.getHeight());
        canvas.concat(viewport.getScreenMatrix());

        drawPaths(canvas, map);
        drawPlateaus(canvas, map);

        return bitmap;
    }

    private void drawPaths(Canvas canvas, GameMap map) {
        Paint pathPaint = new Paint();
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(PATH_COLOR);

        for (MapPath path : map.getPaths()) {
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

    private void drawPlateaus(Canvas canvas, GameMap map) {
        Paint plateauPaint = new Paint();
        plateauPaint.setStyle(Paint.Style.STROKE);
        plateauPaint.setStrokeWidth(1);
        plateauPaint.setColor(PLATEAU_COLOR);

        for (PlateauInfo plateau : map.getPlateaus()) {
            Vector2 position = plateau.getPosition();
            canvas.drawPoint(position.x(), position.y(), plateauPaint);
        }
    }

}
