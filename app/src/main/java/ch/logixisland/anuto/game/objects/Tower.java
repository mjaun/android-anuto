package ch.logixisland.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.TypeIds;
import ch.logixisland.anuto.game.data.Path;
import ch.logixisland.anuto.game.data.TowerConfig;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Intersections;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.Vector2;

public abstract class Tower extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.TOWER;

    /*
    ------ RangeIndicator Class ------
     */

    private class RangeIndicator extends DrawObject {
        private Paint mPen;

        public RangeIndicator() {
            mPen = new Paint();
            mPen.setStyle(Paint.Style.STROKE);
            mPen.setStrokeWidth(0.05f);
            mPen.setColor(Color.GREEN);
            mPen.setAlpha(128);
        }

        @Override
        public int getLayer() {
            return Layers.TOWER_RANGE;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(mPosition.x, mPosition.y, mConfig.range, mPen);
        }
    }

    /*
    ------ PathSection Class ------
     */

    public class PathSection {
        public Vector2 p1;
        public Vector2 p2;
        public float len;
    }

    /*
    ------ Members ------
     */

    protected TowerConfig mConfig;

    protected int mValue;
    protected boolean mReloaded = false;
    protected Plateau mPlateau = null;

    private TickTimer mReloadTimer;
    private RangeIndicator mRangeIndicator;

    /*
    ------ Constructors ------
     */

    public Tower() {
        mConfig = GameManager.getInstance().getLevel().getTowerConfig(this);
        mValue = mConfig.value;
        mReloadTimer = TickTimer.createInterval(mConfig.reload);
    }

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void clean() {
        super.clean();
        hideRange();
        setPlateau(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled && !mReloaded && mReloadTimer.tick()) {
            mReloaded = true;
        }
    }


    public void drawPreview(Canvas canvas) {
    }


    public Plateau getPlateau() {
        return mPlateau;
    }

    public void setPlateau(Plateau plateau) {
        if (mPlateau != null) {
            mPlateau.setOccupant(null);
        }

        mPlateau = plateau;

        if (mPlateau != null) {
            mPlateau.setOccupant(this);
            setPosition(mPlateau.getPosition());
        }
    }


    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

    public float getDamage() {
        return mConfig.damage;
    }

    public float getRange() {
        return mConfig.range;
    }

    public float getReloadTime() {
        return mConfig.reload;
    }

    public boolean isUpgradeable() {
        return mConfig.upgrade != null;
    }

    public int getUpgradeCost() {
        if (mConfig.upgrade == null) {
            return -1;
        }

        return mConfig.upgrade.value;
    }


    public void buy() {
        GameManager.getInstance().takeCredits(mValue);
    }

    public void sell() {
        GameManager.getInstance().giveCredits(mValue);
    }

    public void devalue(float factor) {
        mValue *= factor;
    }

    public Tower upgrade() {
        Plateau plateau = this.getPlateau();
        Tower upgrade;

        try {
            upgrade = mConfig.upgrade.clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        this.remove();
        upgrade.setPlateau(plateau);
        upgrade.setEnabled(true);

        GameManager.getInstance().takeCredits(upgrade.mValue);
        upgrade.mValue += this.mValue;

        mGame.add(upgrade);
        return upgrade;
    }

    public void showRange() {
        if (mRangeIndicator == null) {
            mRangeIndicator = new RangeIndicator();
            mGame.add(mRangeIndicator);
        }
    }

    public void hideRange() {
        if (mRangeIndicator != null) {
            mGame.remove(mRangeIndicator);
            mRangeIndicator = null;
        }
    }


    public StreamIterator<Enemy> getPossibleTargets() {
        return mGame.get(Enemy.TYPE_ID)
                .filter(GameObject.inRange(mPosition, mConfig.range))
                .cast(Enemy.class);
    }

    public List<PathSection> getPathSections() {
        List<PathSection> ret = new ArrayList<>();

        float r2 = MathUtils.square(mConfig.range);

        for (Path p : GameManager.getInstance().getLevel().getPaths()) {
            for (int i = 1; i < p.count(); i++) {
                Vector2 p1 = p.get(i - 1).copy().sub(mPosition);
                Vector2 p2 = p.get(i).copy().sub(mPosition);

                boolean p1in = p1.len2() <= r2;
                boolean p2in = p2.len2() <= r2;

                Vector2[] is = Intersections.lineCircle(p1, p2, mConfig.range);

                PathSection s = new PathSection();

                if (p1in && p2in) {
                    s.p1 = p1.add(mPosition);
                    s.p2 = p2.add(mPosition);
                } else if (!p1in && !p2in) {
                    if (is == null) {
                        continue;
                    }

                    float a1 = Vector2.fromTo(is[0], p1).angle();
                    float a2 = Vector2.fromTo(is[0], p2).angle();

                    if (MathUtils.equals(a1, a2, 10f)) {
                        continue;
                    }

                    s.p1 = is[0].add(mPosition);
                    s.p2 = is[1].add(mPosition);
                }
                else {
                    float angle = Vector2.fromTo(p1, p2).angle();

                    if (p1in) {
                        if (MathUtils.equals(angle, Vector2.fromTo(p1, is[0]).angle(), 10f)) {
                            s.p2 = is[0].add(mPosition);
                        } else {
                            s.p2 = is[1].add(mPosition);
                        }

                        s.p1 = p1.add(mPosition);
                    } else {
                        if (MathUtils.equals(angle, Vector2.fromTo(is[0], p2).angle(), 10f)) {
                            s.p1 = is[0].add(mPosition);
                        } else {
                            s.p1 = is[1].add(mPosition);
                        }

                        s.p2 = p2.add(mPosition);
                    }
                }

                s.len = Vector2.fromTo(s.p1, s.p2).len();
                ret.add(s);
            }
        }

        return ret;
    }
}
