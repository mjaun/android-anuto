package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.objects.Tower;

public class TowerInfoFragment extends Fragment implements View.OnClickListener,
        GameManager.ShowTowerInfoListener, GameManager.HideTowerInfoListener {

    private GameManager mManager;
    private Tower mTower;

    private Button btn_upgrade;
    private Button btn_sell;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tower_info, container, false);

        btn_upgrade = (Button) v.findViewById(R.id.btn_upgrade);
        btn_sell = (Button) v.findViewById(R.id.btn_sell);

        btn_upgrade.setOnClickListener(this);
        btn_sell.setOnClickListener(this);

        btn_upgrade.setEnabled(false);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();

        mManager = ((MainActivity)activity).getManager();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_upgrade) {

        }

        if (v == btn_sell) {
            mTower.sell();
            mManager.hideTowerInfo();
        }
    }

    @Override
    public void onShowTowerInfo(Tower tower) {
        mTower = tower;

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this)
                .commit();
    }

    @Override
    public void onHideTowerInfo() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(this)
                .commit();
    }
}
