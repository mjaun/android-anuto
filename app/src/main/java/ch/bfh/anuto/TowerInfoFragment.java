package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TowerInfoFragment extends Fragment {

    public interface Listener {
        public void onNextWaveClick();
    }

    private Listener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tower_info, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TowerInfoFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }
}
