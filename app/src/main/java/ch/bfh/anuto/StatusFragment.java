package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.data.Wave;

public class StatusFragment extends Fragment implements GameManager.WaveStartedListener,
        GameManager.CreditsChangedListener, GameManager.LivesChangedListener,
        GameManager.GameStartListener, GameManager.GameOverListener,
        GameManager.BonusChangedListener, View.OnClickListener {

    private GameManager mManager;

    private TextView txt_credits;
    private TextView txt_lives;
    private TextView txt_wave;
    private TextView txt_bonus;

    private Button btn_next_wave;
    private Button btn_restart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            mManager.callNextWave();
        }

        if (v == btn_restart) {
            mManager.restart();
        }
    }

    @Override
    public void onWaveStarted(Wave wave) {
        txt_wave.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + " " + mManager.getWaveNumber());
                btn_next_wave.setEnabled(mManager.hasWavesRemaining());
            }
        });
    }

    @Override
    public void onCreditsChanged(final int credits) {
        txt_credits.post(new Runnable() {
            @Override
            public void run() {
                txt_credits.setText(getResources().getString(R.string.status_credits) + " " + credits);
            }
        });
    }

    @Override
    public void onLivesChanged(final int lives) {
        txt_lives.post(new Runnable() {
            @Override
            public void run() {
                txt_lives.setText(getResources().getString(R.string.status_lives) + " " + lives);
            }
        });
    }

    @Override
    public void onGameStart() {
        txt_wave.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + " " + mManager.getWaveNumber());
                btn_next_wave.setEnabled(mManager.hasWavesRemaining());
            }
        });
    }

    @Override
    public void onGameOver(boolean won) {
        btn_next_wave.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void onBonusChanged(final int bonus) {
        txt_bonus.post(new Runnable() {
            @Override
            public void run() {
                txt_bonus.setText(getResources().getString(R.string.status_bonus) + " " + bonus);
            }
        });
    }
}
