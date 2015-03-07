package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

public abstract class GameObject {

    /*
    ------ Members ------
     */

    protected Game mGame = null;
    protected Paint mPaint = new Paint();

    protected PointF mPosition = null;

    /*
    ------ Constructors ------
     */

    public void setGame(Game game) {
        mGame = game;
    }

    /*
    ------ Public Methods ------
     */

    public abstract void tick();

    public abstract void draw(Canvas canvas);

    public PointF getPosition() {
        // TODO: should we make a copy here?
        return mPosition;
    }

    public void setPosition(PointF position) {
        // TODO: is it correct to make a copy here?
        if (position != null) {
            mPosition = new PointF(position.x, position.y);
        }
        else {
            mPosition = null;
        }
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
