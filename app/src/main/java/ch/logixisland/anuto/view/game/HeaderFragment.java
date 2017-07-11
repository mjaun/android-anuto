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

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.GameSpeedListener;
import ch.logixisland.anuto.business.level.GameSpeedManager;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.BonusListener;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.util.StringUtils;
import ch.logixisland.anuto.view.AnutoFragment;
import ch.logixisland.anuto.view.menu.MenuActivity;

public class HeaderFragment extends AnutoFragment implements GameListener, WaveListener,
        CreditsListener, LivesListener, BonusListener, View.OnClickListener, GameSpeedListener {

    private final GameManager mGameManager;
    private final WaveManager mWaveManager;
    private final GameSpeedManager mSpeedManager;
    private final ScoreBoard mScoreBoard;

    private Handler mHandler;

    private TextView txt_credits;
    private TextView txt_lives;
    private TextView txt_wave;
    private TextView txt_bonus;
    private TextView txt_game_speed;

    private Button btn_next_wave;
    private Button btn_dec_rate;
    private Button btn_inc_rate;
    private Button btn_menu;

    private TowerView[] view_tower_x = new TowerView[4];

    public HeaderFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mScoreBoard = factory.getScoreBoard();
        mWaveManager = factory.getWaveManager();
        mSpeedManager = factory.getSpeedManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_header, container, false);

        txt_credits = (TextView) v.findViewById(R.id.txt_credits);
        txt_lives = (TextView) v.findViewById(R.id.txt_lives);
        txt_wave = (TextView) v.findViewById(R.id.txt_wave);
        txt_bonus = (TextView) v.findViewById(R.id.txt_bonus);
        txt_game_speed = (TextView) v.findViewById(R.id.txt_game_speed);

        btn_next_wave = (Button) v.findViewById(R.id.btn_next_wave);
        btn_inc_rate = (Button) v.findViewById(R.id.btn_inc_rate);
        btn_dec_rate = (Button) v.findViewById(R.id.btn_dec_rate);
        btn_menu = (Button) v.findViewById(R.id.btn_menu);

        btn_next_wave.setOnClickListener(this);
        btn_inc_rate.setOnClickListener(this);
        btn_dec_rate.setOnClickListener(this);
        btn_menu.setOnClickListener(this);

        view_tower_x[0] = (TowerView) v.findViewById(R.id.view_tower_1);
        view_tower_x[1] = (TowerView) v.findViewById(R.id.view_tower_2);
        view_tower_x[2] = (TowerView) v.findViewById(R.id.view_tower_3);
        view_tower_x[3] = (TowerView) v.findViewById(R.id.view_tower_4);

        btn_next_wave.setEnabled(!mGameManager.isGameOver());
        txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber());
        txt_credits.setText(getString(R.string.credits) + ": " + StringUtils.formatSuffix(mScoreBoard.getCredits()));
        txt_lives.setText(getString(R.string.lives) + ": " + mScoreBoard.getLives());
        txt_bonus.setText(getString(R.string.bonus) + ": " + StringUtils.formatSuffix(mScoreBoard.getWaveBonus() + mScoreBoard.getEarlyBonus()));

        for (int i = 0; i < view_tower_x.length; i++) {
            view_tower_x[i].setSlot(i);
        }

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGameManager.addListener(this);
        mWaveManager.addListener(this);
        mSpeedManager.addListener(this);
        mScoreBoard.addBonusListener(this);
        mScoreBoard.addCreditsListener(this);
        mScoreBoard.addLivesListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGameManager.removeListener(this);
        mWaveManager.removeListener(this);
        mSpeedManager.removeListener(this);
        mScoreBoard.removeBonusListener(this);
        mScoreBoard.removeCreditsListener(this);
        mScoreBoard.removeLivesListener(this);

        for (TowerView towerView : view_tower_x) {
            towerView.close();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_next_wave) {
            mWaveManager.startNextWave();
        }

        if (v == btn_inc_rate) {
            mSpeedManager.increaseGameSpeed();
        }

        if (v == btn_dec_rate) {
            mSpeedManager.decreaseGameSpeed();
        }

        if (v == btn_menu) {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void waveStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber() + " ("+ mWaveManager.getEnemiesCount()+ ")");
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void nextWaveReady() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(!mGameManager.isGameOver());
            }
        });
    }

    @Override
    public void waveFinished() {

    }

    @Override
    public void creditsChanged(final int credits) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_credits.setText(getString(R.string.credits) + ": " + StringUtils.formatSuffix(credits));
            }
        });
    }

    @Override
    public void livesChanged(final int lives) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_lives.setText(getString(R.string.lives) + ": " + lives);
            }
        });
    }

    @Override
    public void gameStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber());
                btn_next_wave.setEnabled(true);

                for (int i = 0; i < view_tower_x.length; i++) {
                    view_tower_x[i].setSlot(i);
                }
            }
        });
    }

    @Override
    public void gameOver() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void bonusChanged(final int waveBonus, final int earlyBonus) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_bonus.setText(getString(R.string.bonus) + ": " + StringUtils.formatSuffix(waveBonus + earlyBonus));
                txt_wave.setText(getString(R.string.wave) + ": " + mWaveManager.getWaveNumber() + " (" + mWaveManager.getEnemiesCount() + ")");
            }
        });
    }

    @Override
    public void gameSpeedChanged(final int newSpeed, final boolean canIncrease, final boolean canDecrease) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_game_speed.setText(getString(R.string.game_speed) + ": " + newSpeed + "x");
                btn_inc_rate.setEnabled(canIncrease);
                btn_dec_rate.setEnabled(canDecrease);
            }
        });
    }
}
