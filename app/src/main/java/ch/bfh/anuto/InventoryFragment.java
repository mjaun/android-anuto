package ch.bfh.anuto;

import android.app.Activity;
import android.content.ClipData;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ch.bfh.anuto.game.objects.impl.BasicTower;

public class InventoryFragment extends Fragment implements View.OnTouchListener {

    ImageView imgBasicTower;

    public InventoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        imgBasicTower = (ImageView)view.findViewById(R.id.img_BasicTower);
        imgBasicTower.setOnTouchListener(this);

        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

            v.startDrag(data, shadowBuilder, new BasicTower(), 0);

            return true;
        }

        return false;
    }
}
