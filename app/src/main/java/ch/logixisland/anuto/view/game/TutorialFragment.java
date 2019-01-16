package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.TutorialControl;
import ch.logixisland.anuto.view.AnutoFragment;

public class TutorialFragment extends AnutoFragment implements TutorialControl.TutorialView, View.OnClickListener {

    private final TutorialControl mControl;
    private final Handler mHandler;

    private boolean mVisible;

    private TextView txt_content;
    private Button btn_got_it;
    private Button btn_skip;

    public TutorialFragment() {
        mControl = AnutoApplication.getInstance().getGameFactory().getTutorialControl();
        mHandler = new Handler();
        mVisible = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        txt_content = view.findViewById(R.id.txt_content);
        btn_got_it = view.findViewById(R.id.btn_got_it);
        btn_skip = view.findViewById(R.id.btn_skip);

        btn_got_it.setOnClickListener(this);
        btn_skip.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hide();
        mControl.setView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mControl.setView(null);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_got_it) {
            mControl.gotItClicked();
        }

        if (v == btn_skip) {
            mControl.skipClicked();
        }
    }

    @Override
    public void showHint(final int textId, final boolean showSkipButton) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                show(textId, showSkipButton);
            }
        });

    }

    @Override
    public void tutorialFinished() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        });
    }

    private void show(int textId, boolean showSkipButton) {
        txt_content.setText(textId);
        btn_skip.setVisibility(showSkipButton ? View.VISIBLE : View.GONE);

        if (!mVisible) {
            getFragmentManager().beginTransaction()
                    .show(TutorialFragment.this)
                    .commitAllowingStateLoss();

            mVisible = true;
        }
    }

    private void hide() {
        if (mVisible) {
            getFragmentManager().beginTransaction()
                    .hide(TutorialFragment.this)
                    .commitAllowingStateLoss();

            mVisible = false;
        }
    }
}
