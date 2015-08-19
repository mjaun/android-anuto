package ch.logixisland.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.objects.Tower;

public class TowerInfoFragment extends Fragment implements
        View.OnTouchListener, View.OnClickListener,
        GameManager.OnShowTowerInfoListener, GameManager.OnHideTowerInfoListener,
        GameManager.OnCreditsChangedListener {

    private Handler mHandler;
    private GameManager mManager;

    private Tower mTower;
    private boolean mUpgradeable;
    private int mUpgradeCost;

    private TextView txt_value;
    private TextView txt_reload;
    private TextView txt_damage;
    private TextView txt_range;

    private Button btn_upgrade;
    private Button btn_enhance;
    private Button btn_sell;

    private TowerView view_tower;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tower_info, container, false);

        txt_value = (TextView)v.findViewById(R.id.txt_value);
        txt_reload = (TextView)v.findViewById(R.id.txt_reload);
        txt_damage = (TextView)v.findViewById(R.id.txt_damage);
        txt_range = (TextView)v.findViewById(R.id.txt_range);

        btn_upgrade = (Button)v.findViewById(R.id.btn_upgrade);
        btn_enhance = (Button)v.findViewById(R.id.btn_enhance);
        btn_sell = (Button)v.findViewById(R.id.btn_sell);

        view_tower = (TowerView)v.findViewById(R.id.view_tower);

        btn_upgrade.setOnClickListener(this);
        btn_sell.setOnClickListener(this);
        btn_enhance.setOnClickListener(this);

        view_tower.setEnabled(false);

        mHandler = new Handler();

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

        view_tower.close();

        mManager.removeListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_upgrade) {
            mTower = mTower.upgrade();
            mManager.showTowerInfo(mTower);
        }

        if (v == btn_sell) {
            mTower.sell();
            mTower.remove();
            mManager.hideTowerInfo();
        }
    }

    @Override
    public void onShowTowerInfo(Tower tower) {
        mTower = tower;
        mUpgradeable = tower.isUpgradeable();
        mUpgradeCost = tower.getUpgradeCost();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_damage.setText(String.valueOf(mTower.getDamage()));
                txt_value.setText(String.valueOf(mTower.getValue()));
                txt_range.setText(String.valueOf(mTower.getRange()));
                txt_reload.setText(String.valueOf(mTower.getReloadTime()));

                btn_upgrade.setText(getResources().getText(R.string.upgrade) + " (" + mUpgradeCost + ")");
            }
        });

        view_tower.setTowerClass(mTower.getClass());
        onCreditsChanged(mManager.getCredits());

        show();
    }

    @Override
    public void onHideTowerInfo() {
        hide();
    }

    @Override
    public void onCreditsChanged(final int credits) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_upgrade.setEnabled(mUpgradeable && credits >= mUpgradeCost);
            }
        });
    }
}
