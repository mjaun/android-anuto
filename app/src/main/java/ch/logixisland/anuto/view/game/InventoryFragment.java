package ch.logixisland.anuto.view.game;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.level.LevelLoader;

public class InventoryFragment extends Fragment implements GameListener {

    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;

    private TowerView[] view_tower_x = new TowerView[4];

    public InventoryFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mLevelLoader = factory.getLevelLoader();
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mGameManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        for (TowerView aView_tower_x : view_tower_x) {
            aView_tower_x.close();
        }

        mGameManager.removeListener(this);
    }

    @Override
    public void gameStarted() {
        for (int i = 0; i < view_tower_x.length; i++) {
            view_tower_x[i].setTowerClass(mLevelLoader.getLevel().getTowerConfig(i).getTowerClass());
        }
    }

    @Override
    public void gameOver() {

    }
}
