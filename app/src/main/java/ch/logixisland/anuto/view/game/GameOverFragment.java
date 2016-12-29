package ch.logixisland.anuto.view.game;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.render.theme.ThemeManager;

public class GameOverFragment extends Fragment implements GameListener {

    private final ThemeManager mThemeManager;
    private final GameManager mGameManager;
    private final ScoreBoard mScoreBoard;

    private TextView txt_game_over;
    private TextView txt_score;

    public GameOverFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mThemeManager = factory.getThemeManager();
        mGameManager = factory.getGameManager();
        mScoreBoard = factory.getScoreBoard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_over, container, false);

        txt_game_over = (TextView)v.findViewById(R.id.txt_game_over);
        txt_score = (TextView)v.findViewById(R.id.txt_score);

        txt_game_over.setTextColor(mThemeManager.getTheme().getTextColor());
        txt_score.setTextColor(mThemeManager.getTheme().getTextColor());
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();

        mGameManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mGameManager.removeListener(this);
    }

    @Override
    public void gameStarted() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(this)
                .commit();
    }

    @Override
    public void gameOver() {
        txt_game_over.post(new Runnable() {
            @Override
            public void run() {
                txt_game_over.setText(R.string.game_over_lost);

                DecimalFormat fmt = new DecimalFormat("###,###,###,###");
                txt_score.setText(getResources().getString(R.string.score) +
                        ": " + fmt.format(mScoreBoard.getScore()));
            }
        });

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this)
                .commit();
    }
}
