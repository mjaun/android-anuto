package ch.bfh.anuto;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class InventoryItemView extends ImageView implements View.OnTouchListener {

    private Class<?> mItemClass;

    public InventoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.InventoryItemView);

            try {
                mItemClass = Class.forName(a.getString(R.styleable.InventoryItemView_itemClass));
            } catch (ClassNotFoundException e) {}

            a.recycle();

            setOnTouchListener(this);
        }
    }

    public void setItemClass(Class<?> itemClass) {
        mItemClass = itemClass;
    }

    public Class<?> getItemClass() {
        return mItemClass;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Object item;

            try {
                item = mItemClass.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodError("Class " + mItemClass.getName() + " has no default constructor!");
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate object!", e);
            }

            if (item != null) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(this);
                this.startDrag(data, shadowBuilder, item, 0);

                return true;
            }
        }

        return false;
    }
}
