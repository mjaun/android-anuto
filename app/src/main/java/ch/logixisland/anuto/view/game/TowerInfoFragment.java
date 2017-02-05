package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.control.TowerControl;
import ch.logixisland.anuto.business.control.TowerInfo;
import ch.logixisland.anuto.business.control.TowerInfoView;
import ch.logixisland.anuto.business.control.TowerSelector;
import ch.logixisland.anuto.entity.tower.TowerProperty;
import ch.logixisland.anuto.entity.tower.TowerStrategy;
import ch.logixisland.anuto.util.StringUtils;
import ch.logixisland.anuto.view.AnutoFragment;

public class TowerInfoFragment extends AnutoFragment implements View.OnTouchListener,
        View.OnClickListener, TowerInfoView {

    private final TowerSelector mTowerSelector;
    private final TowerControl mTowerControl;

    private Handler mHandler;

    private TextView txt_level;
    private TextView txt_level_text;
    private TextView[] txt_property = new TextView[5];
    private TextView[] txt_property_text = new TextView[5];

    private Button btn_strategy;
    private Button btn_lock_target;
    private Button btn_enhance;
    private Button btn_upgrade;
    private Button btn_sell;

    private boolean mVisible = true;

    public TowerInfoFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mTowerSelector = factory.getTowerSelector();
        mTowerControl = factory.getTowerControl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tower_info, container, false);

        txt_level = (TextView) v.findViewById(R.id.txt_level);
        txt_property[0] = (TextView) v.findViewById(R.id.txt_property1);
        txt_property[1] = (TextView) v.findViewById(R.id.txt_property2);
        txt_property[2] = (TextView) v.findViewById(R.id.txt_property3);
        txt_property[3] = (TextView) v.findViewById(R.id.txt_property4);
        txt_property[4] = (TextView) v.findViewById(R.id.txt_property5);

        txt_level_text = (TextView) v.findViewById(R.id.txt_level_text);
        txt_property_text[0] = (TextView) v.findViewById(R.id.txt_property_text1);
        txt_property_text[1] = (TextView) v.findViewById(R.id.txt_property_text2);
        txt_property_text[2] = (TextView) v.findViewById(R.id.txt_property_text3);
        txt_property_text[3] = (TextView) v.findViewById(R.id.txt_property_text4);
        txt_property_text[4] = (TextView) v.findViewById(R.id.txt_property_text5);

        btn_strategy = (Button) v.findViewById(R.id.btn_strategy);
        btn_lock_target = (Button) v.findViewById(R.id.btn_lock_target);
        btn_upgrade = (Button) v.findViewById(R.id.btn_upgrade);
        btn_enhance = (Button) v.findViewById(R.id.btn_enhance);
        btn_sell = (Button) v.findViewById(R.id.btn_sell);

        btn_strategy.setOnClickListener(this);
        btn_lock_target.setOnClickListener(this);
        btn_enhance.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
        btn_sell.setOnClickListener(this);

        txt_level_text.setText(getResources().getString(R.string.level) + ":");

        mHandler = new Handler();

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTowerSelector.setTowerInfoView(this);
        hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTowerSelector.setTowerInfoView(null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_strategy) {
            mTowerControl.cycleTowerStrategy();
        }

        if (v == btn_lock_target) {
            mTowerControl.toggleLockTarget();
        }

        if (v == btn_enhance) {
            mTowerControl.enhanceTower();
        }

        if (v == btn_upgrade) {
            mTowerControl.upgradeTower();
        }

        if (v == btn_sell) {
            mTowerControl.sellTower();
        }
    }

    @Override
    public void showTowerInfo(final TowerInfo towerInfo) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                show();
                refresh(towerInfo);
            }
        });
    }

    @Override
    public void hideTowerInfo() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        });
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

    private void refresh(TowerInfo towerInfo) {
        txt_level.setText(towerInfo.getLevel() + " / " + towerInfo.getLevelMax());

        List<TowerProperty> properties = towerInfo.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            TowerProperty property = properties.get(i);
            txt_property_text[i].setText(getString(property.getTextId()) + ":");
            txt_property[i].setText(StringUtils.formatSuffix(property.getValue()));
        }
        for (int i = properties.size(); i < txt_property.length; i++) {
            txt_property_text[i].setText("");
            txt_property[i].setText("");
        }

        if (towerInfo.getEnhanceCost() > 0) {
            btn_enhance.setText(StringUtils.formatSwitchButton(
                    getString(R.string.enhance),
                    StringUtils.formatSuffix(towerInfo.getEnhanceCost()))
            );
        } else {
            btn_enhance.setText(getString(R.string.enhance));
        }

        if (towerInfo.getUpgradeCost() > 0) {
            btn_upgrade.setText(StringUtils.formatSwitchButton(
                    getString(R.string.upgrade),
                    StringUtils.formatSuffix(towerInfo.getUpgradeCost()))
            );
        } else {
            btn_upgrade.setText(getString(R.string.upgrade));
        }

        btn_sell.setText(StringUtils.formatSwitchButton(
                getString(R.string.sell),
                StringUtils.formatSuffix(towerInfo.getValue()))
        );

        btn_upgrade.setEnabled(towerInfo.isUpgradeable());
        btn_enhance.setEnabled(towerInfo.isEnhanceable());
        btn_sell.setEnabled(towerInfo.isSellable());

        if (towerInfo.canLockTarget()) {
            btn_lock_target.setText(StringUtils.formatSwitchButton(
                    getString(R.string.lock_target),
                    StringUtils.formatBoolean(towerInfo.doesLockTarget(), getResources()))
            );
            btn_lock_target.setEnabled(true);
        } else {
            btn_lock_target.setText(getString(R.string.lock_target));
            btn_lock_target.setEnabled(false);
        }

        if (towerInfo.hasStrategy()) {
            btn_strategy.setText(StringUtils.formatSwitchButton(
                    getString(R.string.strategy),
                    getStrategyString(towerInfo.getStrategy()))
            );
            btn_strategy.setEnabled(true);
        } else {
            btn_strategy.setText(getString(R.string.strategy));
            btn_strategy.setEnabled(false);
        }
    }

    private String getStrategyString(TowerStrategy strategy) {
        switch (strategy) {
            case Closest:
                return getString(R.string.strategy_closest);

            case Weakest:
                return getString(R.string.strategy_weakest);

            case Strongest:
                return getString(R.string.strategy_strongest);

            case First:
                return getString(R.string.strategy_first);

            case Last:
                return getString(R.string.strategy_last);
        }

        throw new RuntimeException("Unknown strategy!");
    }
}
