package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameSpeed;
import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.business.wave.WaveManager;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.util.StringUtils;
import ch.logixisland.anuto.view.AnutoFragment;

public class HeaderFragment extends AnutoFragment implements WaveManager.Listener, ScoreBoard.Listener,
        GameSpeed.Listener, View.OnClickListener {

    private final WaveManager mWaveManager;
    private final GameSpeed mGameSpeed;
    private final ScoreBoard mScoreBoard;
    private final TowerSelector mTowerSelector;
    private final ThemeManager mThemeManager;

    private Handler mHandler;

    private View fragment_header;

    private TextView txt_credits;
    private TextView txt_lives;
    private TextView txt_wave;
    private TextView txt_bonus;

    private Button btn_next_wave;
    private Button btn_fast_forward_speed;
    private Button btn_fast_forward_active;
    private Button btn_menu;
    private Button btn_build_tower;

    private TowerViewControl mTowerViewControl;

    public HeaderFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mScoreBoard = factory.getScoreBoard();
        mWaveManager = factory.getWaveManager();
        mGameSpeed = factory.getSpeedManager();
        mTowerSelector = factory.getTowerSelector();
        mThemeManager = factory.getThemeManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_header, container, false);

        fragment_header = v;
        txt_credits = v.findViewById(R.id.txt_credits);
        txt_lives = v.findViewById(R.id.txt_lives);
        txt_wave = v.findViewById(R.id.txt_wave);
        txt_bonus = v.findViewById(R.id.txt_bonus);

        btn_next_wave = v.findViewById(R.id.btn_next_wave);
        btn_fast_forward_speed = v.findViewById(R.id.btn_fast_forward_speed);
        btn_fast_forward_active = v.findViewById(R.id.btn_fast_forward_active);
        btn_menu = v.findViewById(R.id.btn_menu);
        btn_build_tower = v.findViewById(R.id.btn_build_tower);

        btn_next_wave.setOnClickListener(this);
        btn_fast_forward_speed.setOnClickListener(this);
        btn_fast_forward_active.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
        btn_build_tower.setOnClickListener(this);
        fragment_header.setOnClickListener(this);

        btn_next_wave.setEnabled(mWaveManager.isNextWaveReady());
        txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber());
        txt_credits.setText(getString(R.string.credits) + ": " + StringUtils.formatSuffix(mScoreBoard.getCredits()));
        txt_lives.setText(getString(R.string.lives) + ": " + mScoreBoard.getLives());
        txt_bonus.setText(getString(R.string.bonus) + ": " + StringUtils.formatSuffix(mScoreBoard.getWaveBonus() + mScoreBoard.getEarlyBonus()));
        btn_fast_forward_speed.setText(getString(R.string.var_speed, mGameSpeed.fastForwardMultiplier()));
        updateButtonFastForwardActive();

        final List<TowerView> towerViews = new ArrayList<>();
        towerViews.add(v.findViewById(R.id.view_tower_1));
        towerViews.add(v.findViewById(R.id.view_tower_2));
        towerViews.add(v.findViewById(R.id.view_tower_3));
        towerViews.add(v.findViewById(R.id.view_tower_4));
        mTowerViewControl = new TowerViewControl(towerViews);

        fragment_header.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            View lastTowerView = towerViews.get(towerViews.size() - 1);
            boolean enoughSpace = lastTowerView.getX() + lastTowerView.getWidth() < btn_menu.getX();

            btn_build_tower.setVisibility(enoughSpace ? View.INVISIBLE : View.VISIBLE);
            for (TowerView towerView : towerViews) {
                towerView.setVisibility(enoughSpace ? View.VISIBLE : View.INVISIBLE);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mWaveManager.addListener(this);
        mGameSpeed.addListener(this);
        mScoreBoard.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mTowerViewControl.close();

        mWaveManager.removeListener(this);
        mGameSpeed.removeListener(this);
        mScoreBoard.removeListener(this);

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        if (view == fragment_header) {
            mTowerSelector.selectTower(null);
            return;
        }

        if (view == btn_next_wave) {
            mWaveManager.startNextWave();
            return;
        }

        if (view == btn_fast_forward_speed) {
            mGameSpeed.cycleFastForward();
            return;
        }

        if (view == btn_fast_forward_active) {
            mGameSpeed.setFastForwardActive(!mGameSpeed.isFastForwardActive());
            return;
        }

        if (view == btn_menu) {
            mTowerSelector.selectTower(null);
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            startActivity(intent);
            return;
        }

        if (view == btn_build_tower) {
            mTowerSelector.toggleTowerBuildView();
            return;
        }
    }

    @Override
    public void waveStarted() {

    }

    @Override
    public void waveNumberChanged() {
        mHandler.post(() -> txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber() + " (" + mWaveManager.getRemainingEnemiesCount() + ")"));
    }

    @Override
    public void nextWaveReadyChanged() {
        mHandler.post(() -> btn_next_wave.setEnabled(mWaveManager.isNextWaveReady()));
    }

    @Override
    public void remainingEnemiesCountChanged() {
        mHandler.post(() -> txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber() + " (" + mWaveManager.getRemainingEnemiesCount() + ")"));
    }

    @Override
    public void creditsChanged(final int credits) {
        mHandler.post(() -> txt_credits.setText(getString(R.string.credits) + ": " + StringUtils.formatSuffix(credits)));
    }

    @Override
    public void livesChanged(final int lives) {
        mHandler.post(() -> txt_lives.setText(getString(R.string.lives) + ": " + lives));
    }

    @Override
    public void bonusChanged(final int waveBonus, final int earlyBonus) {
        mHandler.post(() -> txt_bonus.setText(getString(R.string.bonus) + ": " + StringUtils.formatSuffix(waveBonus + earlyBonus)));
    }

    @Override
    public void gameSpeedChanged() {
        mHandler.post(() -> {
            btn_fast_forward_speed.setText(getString(R.string.var_speed, mGameSpeed.fastForwardMultiplier()));
            updateButtonFastForwardActive();
        });
    }

    private void updateButtonFastForwardActive() {
        if (mGameSpeed.isFastForwardActive()) {
            btn_fast_forward_active.setTextColor(mThemeManager.getTheme().getColor(R.attr.textActiveColor));
        } else {
            btn_fast_forward_active.setTextColor(mThemeManager.getTheme().getColor(R.attr.textColor));
        }
    }
}
