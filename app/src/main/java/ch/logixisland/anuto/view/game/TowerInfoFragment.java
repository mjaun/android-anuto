package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.Preferences;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.tower.TowerControl;
import ch.logixisland.anuto.business.tower.TowerInfo;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.entity.tower.TowerInfoValue;
import ch.logixisland.anuto.entity.tower.TowerStrategy;
import ch.logixisland.anuto.util.StringUtils;
import ch.logixisland.anuto.view.AnutoFragment;

public class TowerInfoFragment extends AnutoFragment implements View.OnClickListener,
        TowerSelector.TowerInfoView {
    private final SharedPreferences mPreferences;

    private final TowerSelector mTowerSelector;
    private final TowerControl mTowerControl;

    private Handler mHandler;

    private TextView txt_level;
    private TextView[] txt_property = new TextView[6];
    private TextView[] txt_property_text = new TextView[6];

    private Button btn_strategy;
    private Button btn_relay_strategy;
    private Button btn_lock_target;
    private Button btn_relay_lock_target;
    private Button btn_enhance;
    private Button btn_enhance_max;
    private Button btn_relay_enhance;
    private Button btn_upgrade;
    private Button btn_upgrade_max;
    private Button btn_relay_upgrade;
    private Button btn_sell;

    private boolean mVisible = true;

    public TowerInfoFragment() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(AnutoApplication.getContext());

        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mTowerSelector = factory.getTowerSelector();
        mTowerControl = factory.getTowerControl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tower_info, container, false);

        txt_level = v.findViewById(R.id.txt_level);
        txt_property[0] = v.findViewById(R.id.txt_property1);
        txt_property[1] = v.findViewById(R.id.txt_property2);
        txt_property[2] = v.findViewById(R.id.txt_property3);
        txt_property[3] = v.findViewById(R.id.txt_property4);
        txt_property[4] = v.findViewById(R.id.txt_property5);
        txt_property[5] = v.findViewById(R.id.txt_property6);

        TextView txt_level_text = v.findViewById(R.id.txt_level_text);
        txt_level_text.setText(getResources().getString(R.string.level) + ":");

        txt_property_text[0] = v.findViewById(R.id.txt_property_text1);
        txt_property_text[1] = v.findViewById(R.id.txt_property_text2);
        txt_property_text[2] = v.findViewById(R.id.txt_property_text3);
        txt_property_text[3] = v.findViewById(R.id.txt_property_text4);
        txt_property_text[4] = v.findViewById(R.id.txt_property_text5);
        txt_property_text[5] = v.findViewById(R.id.txt_property_text6);

        btn_strategy = v.findViewById(R.id.btn_strategy);
        btn_relay_strategy = v.findViewById(R.id.btn_relay_strategy);
        btn_lock_target = v.findViewById(R.id.btn_lock_target);
        btn_relay_lock_target = v.findViewById(R.id.btn_relay_lock_target);
        btn_enhance = v.findViewById(R.id.btn_enhance);
        btn_enhance_max = v.findViewById(R.id.btn_enhance_max);
        btn_relay_enhance = v.findViewById(R.id.btn_relay_enhance);
        btn_upgrade = v.findViewById(R.id.btn_upgrade);
        btn_upgrade_max = v.findViewById(R.id.btn_upgrade_max);
        btn_relay_upgrade = v.findViewById(R.id.btn_relay_upgrade);
        btn_sell = v.findViewById(R.id.btn_sell);

        btn_strategy.setOnClickListener(this);
        btn_relay_strategy.setOnClickListener(this);
        btn_lock_target.setOnClickListener(this);
        btn_relay_lock_target.setOnClickListener(this);
        btn_enhance.setOnClickListener(this);
        btn_enhance_max.setOnClickListener(this);
        btn_relay_enhance.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
        btn_upgrade_max.setOnClickListener(this);
        btn_relay_upgrade.setOnClickListener(this);
        btn_sell.setOnClickListener(this);

        mHandler = new Handler();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TowerInfo towerInfo = mTowerSelector.getTowerInfo();

        if (towerInfo != null) {
            refresh(towerInfo);
            show();
        }
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
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_strategy) {
            mTowerControl.cycleTowerStrategy();
            return;
        }

        if (v == btn_relay_strategy) {
            mTowerControl.relayTowerStrategy();
            return;
        }

        if (v == btn_lock_target) {
            mTowerControl.toggleLockTarget();
            return;
        }

        if (v == btn_relay_lock_target) {
            mTowerControl.relayLockTarget();
            return;
        }

        if (v == btn_enhance) {
            mTowerControl.enhanceTower();
            return;
        }

        if (v == btn_enhance_max) {
            mTowerControl.enhanceTowerMax();
            return;
        }

        if (v == btn_relay_enhance) {
            mTowerControl.relayEnhanceTower();
            return;
        }

        if (v == btn_upgrade) {
            mTowerControl.upgradeTower();
            return;
        }

        if (v == btn_upgrade_max) {
            mTowerControl.upgradeTowerMax();
            return;
        }

        if (v == btn_relay_upgrade) {
            mTowerControl.relayUpgradeTower();
            return;
        }

        if (v == btn_sell) {
            mTowerControl.sellTower();
            return;
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
            int visible = mPreferences.getBoolean(Preferences.MAX_BUTTONS_ENABLED, false) ? View.VISIBLE : View.GONE;
            btn_enhance_max.setVisibility(visible);
            btn_upgrade_max.setVisibility(visible);

            visible = mPreferences.getBoolean(Preferences.RELAY_BUTTONS_ENABLED, false) ? View.VISIBLE : View.GONE;
            btn_relay_strategy.setVisibility(visible);
            btn_relay_lock_target.setVisibility(visible);
            btn_relay_enhance.setVisibility(visible);
            btn_relay_upgrade.setVisibility(visible);

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

    private void refresh(TowerInfo towerInfo) {
        DecimalFormat fmt = new DecimalFormat();
        String level = fmt.format(towerInfo.getLevel()) + " / " + fmt.format(towerInfo.getLevelMax());
        txt_level.setText(level);

        List<TowerInfoValue> properties = towerInfo.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            TowerInfoValue property = properties.get(i);
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

        boolean bSet = towerInfo.isEnhanceable();
        btn_enhance.setEnabled(bSet);
        btn_enhance_max.setEnabled(bSet);
        btn_relay_enhance.setEnabled(bSet);

        bSet = towerInfo.isUpgradeable();
        btn_upgrade.setEnabled(bSet);
        btn_upgrade_max.setEnabled(bSet);
        //btn_relay_upgrade.setEnabled(bSet);

        btn_sell.setEnabled(towerInfo.isSellable());

        if (towerInfo.canLockTarget()) {
            btn_lock_target.setText(StringUtils.formatSwitchButton(
                    getString(R.string.lock_target),
                    StringUtils.formatBoolean(towerInfo.doesLockTarget(), getResources()))
            );
            btn_lock_target.setEnabled(true);
            btn_relay_lock_target.setEnabled(true);
        } else {
            btn_lock_target.setText(getString(R.string.lock_target));
            btn_lock_target.setEnabled(false);
            btn_relay_lock_target.setEnabled(false);
        }

        if (towerInfo.hasStrategy()) {
            btn_strategy.setText(StringUtils.formatSwitchButton(
                    getString(R.string.strategy),
                    getStrategyString(towerInfo.getStrategy()))
            );
            btn_strategy.setEnabled(true);
            btn_relay_strategy.setEnabled(true);
        } else {
            btn_strategy.setText(getString(R.string.strategy));
            btn_strategy.setEnabled(false);
            btn_relay_strategy.setEnabled(false);
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
