package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.data.Wave;

public class StatusFragment extends Fragment implements GameManager.OnWaveStartedListener,
        GameManager.OnCreditsChangedListener, GameManager.OnLivesChangedListener,
        GameManager.OnGameStartedListener, GameManager.OnGameOverListener,
        GameManager.OnBonusChangedListener, GameManager.OnNextWaveReadyListener,
        View.OnClickListener {

    private static final int WAVE_WAIT_TIME = 5000;

    private GameManager mManager;
    private Handler mHandler;

    private TextView txt_credits;
    private TextView txt_lives;
    private TextView txt_wave;
    private TextView txt_bonus;

    private Button btn_next_wave;
    private Button btn_restart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_status, container, false);

        txt_credits = (TextView) v.findViewById(R.id.txt_credits);
        txt_lives = (TextView) v.findViewById(R.id.txt_lives);
        txt_wave = (TextView) v.findViewById(R.id.txt_wave);
        txt_bonus = (TextView) v.findViewById(R.id.txt_bonus);

        btn_next_wave = (Button) v.findViewById(R.id.btn_next_wave);
        btn_restart = (Button) v.findViewById(R.id.btn_restart);

        btn_next_wave.setOnClickListener(this);
        btn_restart.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mManager = ((MainActivity) activity).getManager();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_next_wave) {
            mManager.startNextWave();
        }

        if (v == btn_restart) {
            mManager.restart();
        }
    }

    @Override
    public void onWaveStarted(Wave wave) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + " " + mManager.getWaveNumber());
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void onNextWaveReady(Wave wave) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(true);
            }
        });
    }

    @Override
    public void onCreditsChanged(final int credits) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_credits.setText(getResources().getString(R.string.status_credits) + " " + credits);
            }
        });
    }

    @Override
    public void onLivesChanged(final int lives) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_lives.setText(getResources().getString(R.string.status_lives) + " " + lives);
            }
        });
    }

    @Override
    public void onGameStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + " " + mManager.getWaveNumber());
                btn_next_wave.setEnabled(mManager.hasNextWave());
            }
        });
    }

    @Override
    public void onGameOver(boolean won) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void onBonusChanged(int bonus) {
        int waveReward = (mManager.hasCurrentWave()) ? mManager.getCurrentWave().getWaveReward() : 0;
        final int totalBonus = bonus + waveReward;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_bonus.setText(getResources().getString(R.string.status_bonus) + " " + totalBonus);
            }
        });
    }
}
