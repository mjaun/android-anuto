package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.view.AnutoFragment;

public class TowerBuildFragment extends AnutoFragment implements TowerSelector.TowerBuildView {

    private final TowerSelector mTowerSelector;

    private Handler mHandler;

    private boolean mVisible = true;
    private TowerViewControl mTowerViewControl;

    public TowerBuildFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mTowerSelector = factory.getTowerSelector();
    }

    @Override
    public void toggleTowerBuildView() {
        mHandler.post(() -> {
            if (mVisible) {
                hide();
            } else {
                show();
            }
        });
    }

    @Override
    public void hideTowerBuildView() {
        mHandler.post(this::hide);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_tower_build, container, false);

        List<TowerView> towerViews = new ArrayList<>();
        towerViews.add(v.findViewById(R.id.view_tower_1));
        towerViews.add(v.findViewById(R.id.view_tower_2));
        towerViews.add(v.findViewById(R.id.view_tower_3));
        towerViews.add(v.findViewById(R.id.view_tower_4));
        mTowerViewControl = new TowerViewControl(towerViews);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mTowerSelector.setTowerBuildView(this);
        hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mTowerViewControl.close();

        mTowerSelector.setTowerBuildView(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void show() {
        if (!mVisible) {
            updateMenuTransparency();

            getFragmentManager().beginTransaction()
                    .show(this)
                    .commitAllowingStateLoss();

            mVisible = true;
        }
    }

    private void hide() {
        if (mVisible) {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .commitAllowingStateLoss();

            mVisible = false;
        }
    }
}
