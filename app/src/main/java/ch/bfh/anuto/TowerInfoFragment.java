package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.objects.Tower;

public class TowerInfoFragment extends Fragment implements
        View.OnTouchListener, View.OnClickListener,
        GameManager.OnShowTowerInfoListener, GameManager.OnHideTowerInfoListener {

    private GameManager mManager;
    private Tower mTower;

    private TextView txt_value;
    private TextView txt_reload;
    private TextView txt_damage;
    private TextView txt_range;

    private Button btn_upgrade;
    private Button btn_sell;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tower_info, container, false);

        txt_value = (TextView)v.findViewById(R.id.txt_value);
        txt_reload = (TextView)v.findViewById(R.id.txt_reload);
        txt_damage = (TextView)v.findViewById(R.id.txt_damage);
        txt_range = (TextView)v.findViewById(R.id.txt_range);

        btn_upgrade = (Button)v.findViewById(R.id.btn_upgrade);
        btn_sell = (Button)v.findViewById(R.id.btn_sell);

        btn_upgrade.setOnClickListener(this);
        btn_sell.setOnClickListener(this);

        btn_upgrade.setEnabled(false);

        return v;
    }

    private void show() {
        getFragmentManager().beginTransaction()
                .show(this)
                .commit();
    }

    private void hide() {
        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        hide();

        mManager = GameManager.getInstance();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_upgrade) {
            // TODO
        }

        if (v == btn_sell) {
            mTower.sell();
            mManager.hideTowerInfo();
        }
    }

    @Override
    public void onShowTowerInfo(Tower tower) {
        mTower = tower;

        txt_value.post(new Runnable() {
            @Override
            public void run() {
                txt_value.setText(String.valueOf(mTower.getValue()));
                txt_range.setText(String.valueOf(mTower.getRange()));
                txt_reload.setText(String.valueOf(mTower.getReloadTime()));
            }
        });

        show();
    }

    @Override
    public void onHideTowerInfo() {
        hide();
    }
}
