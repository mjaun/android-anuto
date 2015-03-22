package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.simpleframework.xml.Element;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.data.Path;
import ch.bfh.anuto.util.Vector2;


public abstract class Enemy extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 2;

    private static final float HEALTHBAR_WIDTH = 1.0f;
    private static final float HEALTHBAR_HEIGHT = 0.1f;
    private static final float HEALTHBAR_OFFSET = 0.6f;

    /*
    ------ Members -----
     */

    @Element(name="path")
    protected Path mPath = null;
    protected int mWayPointIndex = 0;

    protected float mHealth = 100f;
    protected float mHealthMax = 100f;
    protected float mSpeed = 1.0f;

    protected Paint mHealthBarBg;
    protected Paint mHealthBarFg;

    /*
    ------ Constructors ------
     */

    protected Enemy() {
        mHealthBarBg = new Paint();
        mHealthBarBg.setColor(Color.BLACK);
        mHealthBarFg = new Paint();
        mHealthBarFg.setColor(Color.GREEN);
    }

    /*
    ------ Public Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        if (!hasWayPoint()) {
            remove();
            return;
        }

        if (getDistanceTo(getWayPoint()) < mSpeed) {
            setPosition(getWayPoint());
            nextWayPoint();
        }
        else {
            move(getDirectionTo(getWayPoint()), mSpeed);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawHealthBar(canvas);
        mSprite.draw(canvas);
    }

    protected void drawHealthBar(Canvas canvas) {
        canvas.save();
        canvas.translate(-HEALTHBAR_WIDTH/2f, HEALTHBAR_OFFSET);

        canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
        canvas.drawRect(0, 0, mHealth * HEALTHBAR_WIDTH / mHealthMax, HEALTHBAR_HEIGHT, mHealthBarFg);

        canvas.restore();
    }


    protected Vector2 getWayPoint() {
        return mPath.getWayPoints().get(mWayPointIndex);
    }

    protected void nextWayPoint() {
        mWayPointIndex++;
    }

    protected boolean hasWayPoint() {
        return mPath != null && mPath.getWayPoints().size() > mWayPointIndex;
    }


    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
        mWayPointIndex = 0;
    }


    public void damage(float dmg) {
        mHealth -= dmg;

        if (mHealth <= 0) {
            remove();
        }
    }

    public void heal(float val) {
        mHealth += val;
    }

    public float getHealth() {
        return mHealth;
    }
}
