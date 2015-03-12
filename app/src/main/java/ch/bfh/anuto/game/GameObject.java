package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

public abstract class GameObject {

    /*
    ------ Members ------
     */

    protected GameEngine mGame = null;
    protected Paint mPaint = new Paint();

    protected PointF mPosition = null;

    /*
    ------ Constructors ------
     */

    public void setGame(GameEngine game) {
        mGame = game;
    }

    /*
    ------ Public Methods ------
     */

    public abstract void tick();

    public abstract void draw(Canvas canvas);

    public PointF getPosition() {
        return mPosition;
    }

    public void setPosition(PointF position) {
        mPosition = new PointF(position.x, position.y);
    }

    public float getDistanceTo(PointF target) {
        return (float)Math.sqrt(Math.pow(target.x - mPosition.x, 2) + Math.pow(target.y - mPosition.y, 2));
    }

    public PointF getDirectionTo(PointF target) {
        float dist = getDistanceTo(target);
        return new PointF((target.x - mPosition.x) / dist, (target.y - mPosition.y) / dist);
    }

    /*
    ------ XML Serialization ------
     */

    @Attribute(name="x")
    private float getPositionX() {
        return mPosition.x;
    }

    @Attribute(name="x")
    private void setPositionX(float x) {
        if (mPosition == null) {
            mPosition = new PointF(x, 0);
        }
        else {
            mPosition.x = x;
        }
    }

    @Attribute(name="y")
    private float getPositionY() {
        return mPosition.y;
    }

    @Attribute(name="y")
    private void setPositionY(float y) {
        if (mPosition == null) {
            mPosition = new PointF(0, y);
        }
        else {
            mPosition.y = y;
        }
    }

    @Persist
    protected void onXmlSerialize() {
    }

    @Commit
    protected void onXmlDeserialize() {
    }
}
