package ch.logixisland.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import ch.logixisland.anuto.game.GameManager;

public class GameOverFragment extends Fragment implements GameManager.OnGameStartedListener,
        GameManager.OnGameOverListener {

    private GameManager mManager;

    private TextView txt_game_over;
    private TextView txt_score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_over, container, false);

        txt_game_over = (TextView)v.findViewById(R.id.txt_game_over);
        txt_score = (TextView)v.findViewById(R.id.txt_score);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();

        mManager = GameManager.getInstance();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
        mManager = null;
    }

    @Override
    public void onGameStarted() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(this)
                .commit();
    }

    @Override
    public void onGameOver() {
        txt_game_over.post(new Runnable() {
            @Override
            public void run() {
                if (mManager.isGameWon()) {
                    txt_game_over.setText(R.string.game_over_won);
                } else {
                    txt_game_over.setText(R.string.game_over_lost);
                }

                DecimalFormat fmt = new DecimalFormat("###,###,###,###");
                txt_score.setText(getResources().getString(R.string.score) +
                        ": " + fmt.format(mManager.getScore()));
            }
        });

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this)
                .commit();
    }
}
