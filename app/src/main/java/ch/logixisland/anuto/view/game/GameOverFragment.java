package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.view.AnutoFragment;

public class GameOverFragment extends AnutoFragment implements GameListener {

    private final GameManager mGameManager;
    private final ScoreBoard mScoreBoard;

    private Handler mHandler;

    private TextView txt_game_over;
    private TextView txt_score;

    public GameOverFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mScoreBoard = factory.getScoreBoard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_over, container, false);

        txt_game_over = (TextView) v.findViewById(R.id.txt_game_over);
        txt_score = (TextView) v.findViewById(R.id.txt_score);

        mHandler = new Handler();

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGameManager.addListener(this);

        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGameManager.removeListener(this);
    }

    @Override
    public void gameStarted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(GameOverFragment.this)
                        .commit();
            }
        });
    }

    @Override
    public void gameOver() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                txt_game_over.setText(R.string.game_over);

                DecimalFormat fmt = new DecimalFormat("###,###,###,###");
                txt_score.setText(getResources().getString(R.string.score) +
                        ": " + fmt.format(mScoreBoard.getScore()));

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(GameOverFragment.this)
                        .commit();
            }
        });
    }

}
