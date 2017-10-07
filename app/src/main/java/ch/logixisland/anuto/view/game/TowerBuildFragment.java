package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.GameStateListener;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.business.tower.TowerBuildView;
import ch.logixisland.anuto.business.tower.TowerInserter;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.view.AnutoFragment;

public class TowerBuildFragment extends AnutoFragment implements TowerBuildView, GameStateListener,
        CreditsListener, View.OnTouchListener {

    private final GameState mGameState;
    private final TowerSelector mTowerSelector;
    private final TowerInserter mTowerInserter;
    private final ScoreBoard mScoreBoard;

    private Handler mHandler;

    private boolean mVisible = true;

    private TowerView[] view_tower_x = new TowerView[4];

    public TowerBuildFragment() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameState = factory.getGameState();
        mScoreBoard = factory.getScoreBoard();
        mTowerSelector = factory.getTowerSelector();
        mTowerInserter = factory.getTowerInserter();
    }

    @Override
    public void showTowerBuildView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
    }

    @Override
    public void hideTowerBuildView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        });
    }

    private void show() {
        if (!mVisible) {
            updateMenuTransparency();

            getFragmentManager().beginTransaction()
                    .show(this)
                    .commitAllowingStateLoss();

            mVisible = true;
        }
    }

    private void hide() {
        if (mVisible) {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .commitAllowingStateLoss();

            mVisible = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHandler = new Handler();

        View v = inflater.inflate(R.layout.fragment_tower_build, container, false);

        view_tower_x[0] = (TowerView) v.findViewById(R.id.view_tower_1);
        view_tower_x[1] = (TowerView) v.findViewById(R.id.view_tower_2);
        view_tower_x[2] = (TowerView) v.findViewById(R.id.view_tower_3);
        view_tower_x[3] = (TowerView) v.findViewById(R.id.view_tower_4);

        for (TowerView towerView : view_tower_x) {
            towerView.setOnTouchListener(this);
        }

        updateTowerSlots();

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mGameState.addListener(this);
        mScoreBoard.addCreditsListener(this);
        mTowerSelector.setTowerBuildView(this);
        hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mGameState.removeListener(this);
        mScoreBoard.removeCreditsListener(this);
        mTowerSelector.setTowerBuildView(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            TowerView towerView = (TowerView) v;

            if (mScoreBoard.getCredits() >= towerView.getTowerValue()) {
                mTowerInserter.insertTower(towerView.getTowerName());

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder() {
                    @Override
                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                    }

                    @Override
                    public void onDrawShadow(Canvas canvas) {
                    }
                };
                ClipData data = ClipData.newPlainText("", "");
                towerView.startDrag(data, shadowBuilder, towerView, 0);

                hide();
            }
        }

        return false;
    }

    @Override
    public void gameRestart() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                updateTowerSlots();
            }
        });
    }

    @Override
    public void gameOver() {

    }

    @Override
    public void creditsChanged(int credits) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                updateTowerEnabled();
            }
        });
    }

    private void updateTowerSlots() {
        for (int i = 0; i < view_tower_x.length; i++) {
            view_tower_x[i].setPreviewTower(mTowerInserter.createPreviewTower(i));
        }

        updateTowerEnabled();
    }

    private void updateTowerEnabled() {
        for (TowerView towerView : view_tower_x) {
            towerView.setEnabled(mScoreBoard.getCredits() >= towerView.getTowerValue());
        }
    }
}
