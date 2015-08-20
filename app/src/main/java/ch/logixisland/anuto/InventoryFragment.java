package ch.logixisland.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.logixisland.anuto.game.GameManager;

public class InventoryFragment extends Fragment implements GameManager.OnGameStartedListener {

    private final static String TAG = InventoryFragment.class.getSimpleName();

    TowerView[] view_tower_x = new TowerView[4];

    GameManager mManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        view_tower_x[0] = (TowerView)v.findViewById(R.id.view_tower_1);
        view_tower_x[1] = (TowerView)v.findViewById(R.id.view_tower_2);
        view_tower_x[2] = (TowerView)v.findViewById(R.id.view_tower_3);
        view_tower_x[3] = (TowerView)v.findViewById(R.id.view_tower_4);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mManager = GameManager.getInstance();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        for (int i = 0; i < view_tower_x.length; i++) {
            view_tower_x[i].close();
        }

        mManager.removeListener(this);
    }

    @Override
    public void onGameStarted() {
        for (int i = 0; i < view_tower_x.length; i++) {
            view_tower_x[i].setTowerClass(mManager.getLevel().getTowerConfig(i).clazz);
        }
    }
}
