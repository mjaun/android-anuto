package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.logixisland.anuto.Preferences;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.tower.TowerInserter;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.business.wave.WaveManager;

public class TutorialControl implements TowerInserter.Listener, WaveManager.WaveStartedListener, TowerSelector.Listener {

    public interface TutorialView {
        void showHint(int textId, boolean showSkipButton);
        void tutorialFinished();
    }

    private enum State {
        Welcome,
        Credits,
        Towers1,
        Towers2,
        Towers3,
        Towers4,
        NextWave,
        Enemies,
        Finished,
        Idle;

        public State next() {
            State[] vals = values();
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    private final SharedPreferences mPreferences;

    private TutorialView mView;
    private State mState;

    public TutorialControl(Context context, TowerInserter towerInserter, TowerSelector towerSelector, WaveManager waveManager) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        towerInserter.addListener(this);
        waveManager.addListener(this);
        towerSelector.addListener(this);
    }

    public void restart() {
        mPreferences.edit()
                .putBoolean(Preferences.TUTORIAL_ENABLED, true)
                .apply();

        initialize();
    }

    public void setView(TutorialView view) {
        mView = view;

        if (mView != null) {
            initialize();
        }
    }

    public void gotItClicked() {
        mState = mState.next();
        activate();

        if (mState == State.Idle) {
            mPreferences.edit()
                    .putBoolean(Preferences.TUTORIAL_ENABLED, false)
                    .apply();
        }
    }

    public void skipClicked() {
        mState = State.Finished;
        activate();
    }

    @Override
    public void towerInserted() {
        if (mState == State.Welcome) {
            mState = mState.next();
            activate();
        }
    }

    @Override
    public void towerInfoShown() {
        if (mState == State.Towers1) {
            mState = mState.next();
            activate();
        }
    }

    @Override
    public void waveStarted() {
        if (mState == State.NextWave) {
            mState = mState.next();
            activate();
        }
    }

    private void initialize() {
        if (mPreferences.getBoolean(Preferences.TUTORIAL_ENABLED, true)) {
            mState = State.Welcome;
        } else {
            mState = State.Idle;
        }

        activate();
    }

    private void activate() {
        switch (mState) {
            case Welcome:
                mView.showHint(R.string.getting_started_welcome, true);
                break;
            case Credits:
                mView.showHint(R.string.getting_started_credits, false);
                break;
            case Towers1:
                mView.showHint(R.string.getting_started_towers1, false);
                break;
            case Towers2:
                mView.showHint(R.string.getting_started_towers2, false);
                break;
            case Towers3:
                mView.showHint(R.string.getting_started_towers3, false);
                break;
            case Towers4:
                mView.showHint(R.string.getting_started_towers4, false);
                break;
            case NextWave:
                mView.showHint(R.string.getting_started_next_wave, false);
                break;
            case Enemies:
                mView.showHint(R.string.getting_started_enemies, false);
                break;
            case Finished:
                mView.showHint(R.string.getting_started_finished, false);
                break;
            default:
                mView.tutorialFinished();
                break;
        }
    }
}
