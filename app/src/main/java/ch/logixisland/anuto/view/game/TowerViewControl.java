package ch.logixisland.anuto.view.game;

import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.GameStateListener;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.business.tower.TowerInserter;

class TowerViewControl implements GameStateListener, CreditsListener, View.OnTouchListener {

    private final ScoreBoard mScoreBoard;
    private final GameState mGameState;
    private final TowerInserter mTowerInserter;

    private final Handler mHandler;
    private final List<TowerView> mTowerViews;


    TowerViewControl(List<TowerView> towerViews) {
        mTowerViews = towerViews;

        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mScoreBoard = factory.getScoreBoard();
        mGameState = factory.getGameState();
        mTowerInserter = factory.getTowerInserter();

        mHandler = new Handler();

        mGameState.addListener(this);
        mScoreBoard.addCreditsListener(this);

        for (TowerView towerView : mTowerViews) {
            towerView.setOnTouchListener(this);
        }

        updateTowerSlots();
    }

    void close() {
        mGameState.removeListener(this);
        mScoreBoard.removeCreditsListener(this);
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
        for (int i = 0; i < mTowerViews.size(); i++) {
            mTowerViews.get(i).setPreviewTower(mTowerInserter.createPreviewTower(i));
        }

        updateTowerEnabled();
    }

    private void updateTowerEnabled() {
        for (TowerView towerView : mTowerViews) {
            towerView.setEnabled(mScoreBoard.getCredits() >= towerView.getTowerValue());
        }
    }
}
