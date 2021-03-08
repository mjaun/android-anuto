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
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.view.AnutoFragment;

public class GameOverFragment extends AnutoFragment implements GameState.Listener {

    private final GameState mGameState;

    private Handler mHandler;

    private TextView txt_score;

    public GameOverFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameState = factory.getGameState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_over, container, false);

        txt_score = v.findViewById(R.id.txt_score);

        mHandler = new Handler();

        // in case it is already game over on initialization
        updateScore();

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mGameState.addListener(this);

        if (!mGameState.isGameOver()) {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .commit();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mGameState.removeListener(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void gameRestart() {
        mHandler.post(() -> getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(GameOverFragment.this)
                .commitAllowingStateLoss());
    }

    @Override
    public void gameOver() {
        mHandler.post(() -> {
            updateScore();

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(GameOverFragment.this)
                    .commitAllowingStateLoss();
        });
    }

    private void updateScore() {
        DecimalFormat fmt = new DecimalFormat("###,###,###,###");
        txt_score.setText(getResources().getString(R.string.score) +
                ": " + fmt.format(mGameState.getFinalScore()));
    }

}
