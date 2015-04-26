package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.bfh.anuto.game.GameManager;

public class InventoryFragment extends Fragment implements GameManager.OnGameOverListener,
        GameManager.OnCreditsChangedListener {

    private GameManager mManager;

    private InventoryItemView img_basic_tower;
    private InventoryItemView img_laser_tower;
    private InventoryItemView img_rocket_tower;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        img_basic_tower = (InventoryItemView)v.findViewById(R.id.img_basic_tower);
        img_laser_tower = (InventoryItemView)v.findViewById(R.id.img_laser_tower);
        img_rocket_tower = (InventoryItemView)v.findViewById(R.id.img_rocket_tower);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mManager = ((MainActivity)activity).getManager();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
    }

    @Override
    public void onGameOver(boolean won) {
        img_basic_tower.post(new Runnable() {
            @Override
            public void run() {
                img_basic_tower.setEnabled(false);
                img_laser_tower.setEnabled(false);
                img_rocket_tower.setEnabled(false);
            }
        });
    }

    @Override
    public void onCreditsChanged(final int credits) {
        if (!mManager.isGameOver()) {
            img_basic_tower.post(new Runnable() {
                @Override
                public void run() {
                    img_basic_tower.setEnabled(credits >= img_basic_tower.getItem().getValue());
                    img_laser_tower.setEnabled(credits >= img_laser_tower.getItem().getValue());
                    img_rocket_tower.setEnabled(credits >= img_rocket_tower.getItem().getValue());
                }
            });
        }
    }
}
