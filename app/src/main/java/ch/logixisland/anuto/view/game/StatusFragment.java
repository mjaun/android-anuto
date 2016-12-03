package ch.logixisland.anuto.view.game;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameFactory;
import ch.logixisland.anuto.game.business.GameManager;
import ch.logixisland.anuto.game.data.Wave;
import ch.logixisland.anuto.game.theme.ThemeManager;
import ch.logixisland.anuto.util.StringUtils;
import ch.logixisland.anuto.game.theme.Theme;

public class StatusFragment extends Fragment implements GameManager.OnWaveStartedListener,
        GameManager.OnCreditsChangedListener, GameManager.OnLivesChangedListener,
        GameManager.OnGameStartedListener, GameManager.OnGameOverListener,
        GameManager.OnBonusChangedListener, GameManager.OnNextWaveReadyListener,
        View.OnClickListener {

    private final ThemeManager mThemeManager;
    private final GameManager mGameManager;

    private Handler mHandler;

    private TextView txt_credits;
    private TextView txt_lives;
    private TextView txt_wave;
    private TextView txt_bonus;

    private Button btn_next_wave;
    private Button btn_restart;

    public StatusFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mThemeManager = factory.getThemeManager();
        mGameManager = factory.getGameManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_status, container, false);
        Theme theme = mThemeManager.getTheme();
        v.setBackgroundColor(theme.getBackgroundColor());

        txt_credits = (TextView) v.findViewById(R.id.txt_credits);
        txt_lives = (TextView) v.findViewById(R.id.txt_lives);
        txt_wave = (TextView) v.findViewById(R.id.txt_wave);
        txt_bonus = (TextView) v.findViewById(R.id.txt_bonus);

        txt_credits.setTextColor(theme.getTextColor());
        txt_lives.setTextColor(theme.getTextColor());
        txt_wave.setTextColor(theme.getTextColor());
        txt_bonus.setTextColor(theme.getTextColor());

        btn_next_wave = (Button) v.findViewById(R.id.btn_next_wave);
        btn_restart = (Button) v.findViewById(R.id.btn_restart);

        btn_next_wave.setOnClickListener(this);
        btn_restart.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGameManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGameManager.removeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_next_wave) {
            mGameManager.startNextWave();
        }

        if (v == btn_restart) {
            if (mGameManager.isGameOver()) {
                mGameManager.restart();
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mGameManager.restart();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.msg_are_you_sure)
                        .setPositiveButton(android.R.string.yes, dialogClickListener)
                        .setNegativeButton(android.R.string.no, dialogClickListener)
                        .show();
            }
        }
    }

    @Override
    public void onWaveStarted(Wave wave) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + ": " + mGameManager.getWaveNumber());
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void onNextWaveReady(Wave wave) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(!mGameManager.isGameOver());
            }
        });
    }

    @Override
    public void onCreditsChanged(final int credits) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_credits.setText(getResources().getString(R.string.status_credits) + ": " + StringUtils.formatSuffix(credits));
            }
        });
    }

    @Override
    public void onLivesChanged(final int lives) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_lives.setText(getResources().getString(R.string.status_lives) + ": " + lives);
            }
        });
    }

    @Override
    public void onGameStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + ": " + mGameManager.getWaveNumber());
                btn_next_wave.setEnabled(mGameManager.hasNextWave());
            }
        });
    }

    @Override
    public void onGameOver() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void onBonusChanged(final int bonus, final int earlyBonus) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_bonus.setText(String.format("%s: %s (+%s)",
                        getResources().getString(R.string.status_bonus),
                        StringUtils.formatSuffix(bonus),
                        StringUtils.formatSuffix(earlyBonus)));
            }
        });
    }
}
