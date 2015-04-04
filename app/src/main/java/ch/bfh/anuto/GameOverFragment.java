package ch.bfh.anuto;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameManager;

public class GameOverFragment extends Fragment implements GameManager.Listener {

    private GameEngine mGame;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_over, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();

        mGame = ((MainActivity)activity).getGame();
        mGame.getManager().addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mGame.getManager().removeListener(this);
    }

    @Override
    public void onWaveChanged() {

    }

    @Override
    public void onCreditsChanged() {

    }

    @Override
    public void onLivesChanged() {

    }

    @Override
    public void onGameOver() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this)
                .commit();
    }
}
