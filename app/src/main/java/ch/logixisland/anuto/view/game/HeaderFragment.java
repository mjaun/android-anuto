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
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.score.BonusListener;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.util.StringUtils;
import ch.logixisland.anuto.engine.render.theme.Theme;

public class HeaderFragment extends Fragment implements GameListener, WaveListener, CreditsListener,
        LivesListener, BonusListener, View.OnClickListener {

    private final ThemeManager mThemeManager;
    private final GameManager mGameManager;
    private final WaveManager mWaveManager;
    private final ScoreBoard mScoreBoard;

    private Handler mHandler;

    private TextView txt_credits;
    private TextView txt_lives;
    private TextView txt_wave;
    private TextView txt_bonus;

    private Button btn_next_wave;
    private Button btn_restart;

    private TowerView[] view_tower_x = new TowerView[4];

    public HeaderFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mThemeManager = factory.getThemeManager();
        mGameManager = factory.getGameManager();
        mScoreBoard = factory.getScoreBoard();
        mWaveManager = factory.getWaveManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_header, container, false);
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

        view_tower_x[0] = (TowerView)v.findViewById(R.id.view_tower_1);
        view_tower_x[1] = (TowerView)v.findViewById(R.id.view_tower_2);
        view_tower_x[2] = (TowerView)v.findViewById(R.id.view_tower_3);
        view_tower_x[3] = (TowerView)v.findViewById(R.id.view_tower_4);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGameManager.addListener(this);
        mWaveManager.addListener(this);
        mScoreBoard.addBonusListener(this);
        mScoreBoard.addCreditsListener(this);
        mScoreBoard.addLivesListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGameManager.removeListener(this);
        mWaveManager.removeListeners(this);
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

        if (v == btn_restart) {
            if (mGameManager.isGameOver()) {
                mGameManager.restart();
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
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
    public void waveStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + ": " + mWaveManager.getWaveNumber());
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
                txt_credits.setText(getResources().getString(R.string.status_credits) + ": " + StringUtils.formatSuffix(credits));
            }
        });
    }

    @Override
    public void livesChanged(final int lives) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_lives.setText(getResources().getString(R.string.status_lives) + ": " + lives);
            }
        });
    }

    @Override
    public void gameStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_wave.setText(getResources().getString(R.string.status_wave) + ": " + mWaveManager.getWaveNumber());
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
                txt_bonus.setText(String.format("%s: %s (+%s)",
                        getResources().getString(R.string.status_bonus),
                        StringUtils.formatSuffix(waveBonus),
                        StringUtils.formatSuffix(earlyBonus)));
            }
        });
    }
}
