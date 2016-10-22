package ch.logixisland.anuto.view.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.logixisland.anuto.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelSelectFragment extends Fragment {


    public static final String SELECTED_LEVEL = "ch.logixisland.anuto.view.menu.SELECTED_LEVEL";

    public LevelSelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_level_select, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
