package ch.logixisland.anuto.view.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.tower.TowerInserter;

public class GettingStartedControl implements TowerInserter.Listener {

    public interface GettingStartedView {
        void show(int textId, boolean showGotItButton);
        void hide();
    }

    private final SharedPreferences mPreferences;
    private final TowerInserter mTowerInserter;
    private final GettingStartedView mView;

    public GettingStartedControl(Context context, TowerInserter towerInserter, GettingStartedView view) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mTowerInserter = towerInserter;
        mView = view;

        mTowerInserter.addListener(this);
        mView.show(R.string.getting_started_welcome, false);
    }

    public void gotItClicked() {

    }

    public void release() {
        mTowerInserter.removeListener(this);
    }

    @Override
    public void towerInserted() {
        mView.hide();
    }

}
