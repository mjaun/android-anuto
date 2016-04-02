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

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.StringUtils;

public class TowerInfoFragment extends Fragment implements
        View.OnTouchListener, View.OnClickListener,
        GameManager.OnShowTowerInfoListener, GameManager.OnHideTowerInfoListener,
        GameManager.OnCreditsChangedListener, GameManager.OnTowersAgedListener,
        GameManager.OnGameOverListener {

    private Handler mHandler;
    private GameManager mManager;

    private Tower mTower;

    private TextView txt_level;
    private TextView txt_damage;
    private TextView txt_range;
    private TextView txt_reload;
    private TextView txt_inflicted;

    private TextView txt_level_text;
    private TextView txt_damage_text;
    private TextView txt_range_text;
    private TextView txt_reload_text;
    private TextView txt_inflicted_text;

    private Button btn_strategy;
    private Button btn_lock_target;
    private Button btn_enhance;
    private Button btn_upgrade;
    private Button btn_sell;

    private TowerView view_tower;

    private boolean mVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tower_info, container, false);

        txt_level = (TextView)v.findViewById(R.id.txt_level);
        txt_damage = (TextView)v.findViewById(R.id.txt_damage);
        txt_range = (TextView)v.findViewById(R.id.txt_range);
        txt_reload = (TextView)v.findViewById(R.id.txt_reload);
        txt_inflicted = (TextView)v.findViewById(R.id.txt_inflicted);

        txt_level_text = (TextView)v.findViewById(R.id.txt_level_text);
        txt_damage_text = (TextView)v.findViewById(R.id.txt_damage_text);
        txt_range_text = (TextView)v.findViewById(R.id.txt_range_text);
        txt_reload_text = (TextView)v.findViewById(R.id.txt_reload_text);
        txt_inflicted_text = (TextView)v.findViewById(R.id.txt_inflicted_text);

        btn_strategy = (Button)v.findViewById(R.id.btn_strategy);
        btn_lock_target = (Button)v.findViewById(R.id.btn_lock_target);
        btn_upgrade = (Button)v.findViewById(R.id.btn_upgrade);
        btn_enhance = (Button)v.findViewById(R.id.btn_enhance);
        btn_sell = (Button)v.findViewById(R.id.btn_sell);

        view_tower = (TowerView)v.findViewById(R.id.view_tower);

        btn_strategy.setOnClickListener(this);
        btn_lock_target.setOnClickListener(this);
        btn_enhance.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
        btn_sell.setOnClickListener(this);

        view_tower.setEnabled(false);

        txt_level_text.setText(getResources().getString(R.string.level) + ":");
        txt_range_text.setText(getResources().getString(R.string.range) + ":");
        txt_reload_text.setText(getResources().getString(R.string.reload) + ":");
        txt_inflicted_text.setText(getResources().getString(R.string.inflicted) + ":");

        mHandler = new Handler();

        return v;
    }

    private void show() {
        if (!mVisible) {
            getFragmentManager().beginTransaction()
                    .show(this)
                    .commit();

            mVisible = true;
        }
    }

    private void hide() {
        if (mVisible) {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .commit();

            mVisible = false;
        }
    }

    private void refresh() {
        if (mTower == null) {
            return;
        }

        txt_level.setText(mTower.getLevel() + " / " + mTower.getLevelMax());
        txt_damage.setText(StringUtils.formatSuffix(mTower.getDamage()));
        txt_range.setText(StringUtils.formatSuffix(mTower.getRange()));
        txt_reload.setText(StringUtils.formatSuffix(mTower.getReloadTime()));
        txt_inflicted.setText(StringUtils.formatSuffix(mTower.getDamageInflicted()));

        if (mTower.getConfig().damageText == null) {
            txt_damage_text.setText(getResources().getString(R.string.damage) + ":");
        } else {
            txt_damage_text.setText(mTower.getConfig().damageText + ":");
        }

        if (mTower.isEnhanceable()) {
            btn_enhance.setText(getResources().getString(R.string.enhance)
                    + " (" + StringUtils.formatSuffix(mTower.getEnhanceCost()) + ")");
        } else {
            btn_enhance.setText(getResources().getString(R.string.enhance));
        }

        if (mTower.isUpgradeable()) {
            btn_upgrade.setText(getResources().getString(R.string.upgrade)
                    + " (" + StringUtils.formatSuffix(mTower.getUpgradeCost()) + ")");
        } else {
            btn_upgrade.setText(getResources().getString(R.string.upgrade));
        }

        btn_sell.setText(getResources().getString(R.string.sell)
                + " (" + StringUtils.formatSuffix(mTower.getValue()) + ")");

        if (mTower instanceof AimingTower) {
            AimingTower t = (AimingTower) mTower;
            btn_strategy.setText(getResources().getString(R.string.strategy) + " (" + t.getStrategy().name() + ")");
            btn_lock_target.setText(getResources().getString(R.string.lock_target) + " (" + t.doesLockOnTarget() + ")");
            btn_strategy.setEnabled(true);
            btn_lock_target.setEnabled(true);
        } else {
            btn_strategy.setText(getResources().getString(R.string.strategy));
            btn_lock_target.setText(getResources().getString(R.string.lock_target));
            btn_strategy.setEnabled(false);
            btn_lock_target.setEnabled(false);
        }

        btn_upgrade.setEnabled(mTower.isUpgradeable() && mManager.getCredits() >= mTower.getUpgradeCost());
        btn_enhance.setEnabled(mTower.isEnhanceable() && mManager.getCredits() >= mTower.getEnhanceCost());
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
        if (v == btn_strategy) {
            onStrategyClicked();
        }

        if (v == btn_lock_target) {
            onLockTargetClicked();
        }

        if (v == btn_enhance) {
            onEnhanceClicked();
        }

        if (v == btn_upgrade) {
            onUpgradeClicked();
        }

        if (v == btn_sell) {
            onSellClicked();
        }
    }

    private void onSellClicked() {
        if (mTower != null) {
            view_tower.setTower(null);

            mTower.sell();
            mTower.remove();
            mTower = null;

            mManager.hideTowerInfo();
        }
    }

    private void onUpgradeClicked() {
        if (mTower != null && mTower.isUpgradeable()) {
            mTower = mTower.upgrade();
            mManager.setSelectedTower(mTower);
            mManager.showTowerInfo(mTower);
        }
    }

    private void onEnhanceClicked() {
        if (mTower != null && mTower.isEnhanceable()) {
            mTower.enhance();
            refresh();
        }
    }

    private void onLockTargetClicked() {
        if (mTower != null && mTower instanceof AimingTower) {
            AimingTower t = (AimingTower) mTower;
            t.setLockOnTarget(!t.doesLockOnTarget());

            refresh();
        }
    }

    private void onStrategyClicked() {
        if (mTower != null && mTower instanceof AimingTower) {
            AimingTower t = (AimingTower) mTower;
            List<AimingTower.Strategy> values = Arrays.asList(AimingTower.Strategy.values());
            int index = values.indexOf(t.getStrategy()) + 1;
            if (index >= values.size()) {
                index = 0;
            }
            t.setStrategy(values.get(index));

            refresh();
        }
    }

    @Override
    public void onShowTowerInfo(Tower tower) {
        mTower = tower;
        view_tower.setTowerClass(tower.getClass());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refresh();
                show();
            }
        });
    }

    @Override
    public void onHideTowerInfo() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        });
    }

    @Override
    public void onCreditsChanged(final int credits) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    @Override
    public void onTowersAged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    @Override
    public void onGameOver() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        });
    }
}
