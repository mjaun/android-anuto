package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.view.AnutoFragment;

public class GettingStartedFragment extends AnutoFragment implements GettingStartedControl.GettingStartedView, View.OnClickListener {

    private GettingStartedControl mControl;
    private boolean mVisible;

    private TextView txt_content;
    private Button btn_got_it;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_getting_started, container, false);

        txt_content = view.findViewById(R.id.txt_content);
        btn_got_it = view.findViewById(R.id.btn_got_it);
        btn_got_it.setOnClickListener(this);

        GameFactory gameFactory = AnutoApplication.getInstance().getGameFactory();
        mControl = new GettingStartedControl(getActivity(), gameFactory.getTowerInserter(), this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mControl.release();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_got_it) {
            mControl.gotItClicked();
        }
    }

    @Override
    public void show(int textId, boolean showGotItButton) {
        txt_content.setText(textId);
        btn_got_it.setVisibility(showGotItButton ? View.VISIBLE : View.GONE);

        if (!mVisible) {
            getFragmentManager().beginTransaction()
                    .show(this)
                    .commitAllowingStateLoss();

            mVisible = true;
        }
    }

    @Override
    public void hide() {
        if (mVisible) {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .commitAllowingStateLoss();

            mVisible = false;
        }
    }
}
