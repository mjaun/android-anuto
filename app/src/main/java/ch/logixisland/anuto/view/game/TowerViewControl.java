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
import ch.logixisland.anuto.business.control.TowerInserter;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.entity.tower.TowerFactory;

class TowerViewControl implements GameListener, CreditsListener, View.OnTouchListener {

    private final ScoreBoard mScoreBoard;
    private final GameManager mGameManager;
    private final TowerFactory mTowerFactory;
    private final TowerInserter mTowerInserter;

    private final Handler mHandler;
    private final List<TowerView> mTowerViews;


    TowerViewControl(List<TowerView> towerViews) {
        mTowerViews = towerViews;

        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mScoreBoard = factory.getScoreBoard();
        mGameManager = factory.getGameManager();
        mTowerInserter = factory.getTowerInserter();
        mTowerFactory = factory.getTowerFactory();

        mHandler = new Handler();

        mGameManager.addListener(this);
        mScoreBoard.addCreditsListener(this);

        for (TowerView towerView : mTowerViews) {
            towerView.setOnTouchListener(this);
        }

        updateTowerSlots();
    }

    void close() {
        mGameManager.removeListener(this);
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
            mTowerViews.get(i).setPreviewTower(mTowerFactory.createTower(i));
        }

        updateTowerEnabled();
    }

    private void updateTowerEnabled() {
        for (TowerView towerView : mTowerViews) {
            towerView.setEnabled(mScoreBoard.getCredits() >= towerView.getTowerValue());
        }
    }
}
