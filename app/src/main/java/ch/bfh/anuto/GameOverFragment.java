package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.data.Wave;

public class GameOverFragment extends Fragment implements GameManager.GameStartListener,
        GameManager.GameOverListener {

    private GameManager mManager;

    private TextView txt_game_over;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_over, container, false);

        txt_game_over = (TextView)v.findViewById(R.id.txt_game_over);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();

        mManager = ((MainActivity)activity).getManager();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
    }

    @Override
    public void onGameStart() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(this)
                .commit();
    }

    @Override
    public void onGameOver(final boolean won) {
        txt_game_over.post(new Runnable() {
            @Override
            public void run() {
                if (won) {
                    txt_game_over.setText(R.string.game_over_won);
                } else {
                    txt_game_over.setText(R.string.game_over_lost);
                }
            }
        });

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this)
                .commit();
    }
}
