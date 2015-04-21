package ch.bfh.anuto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.data.Wave;

public class InventoryFragment extends Fragment implements View.OnClickListener,
        GameManager.GameListener, GameManager.WaveListener, GameManager.CreditsListener {

    private GameManager mManager;

    private InventoryItemView img_basic_tower;
    private InventoryItemView img_laser_tower;
    private InventoryItemView img_rocket_tower;
    private Button btn_next_wave;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        img_basic_tower = (InventoryItemView)v.findViewById(R.id.img_basic_tower);
        img_laser_tower = (InventoryItemView)v.findViewById(R.id.img_laser_tower);
        img_rocket_tower = (InventoryItemView)v.findViewById(R.id.img_rocket_tower);

        btn_next_wave = (Button)v.findViewById(R.id.btn_next_wave);
        btn_next_wave.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mManager = ((MainActivity)activity).getManager();
        mManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mManager.removeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_next_wave) {
            mManager.callNextWave();
        }
    }


    @Override
    public void onGameStart() {
        if (mManager.hasWavesRemaining()) {
            btn_next_wave.post(new Runnable() {
                @Override
                public void run() {
                    btn_next_wave.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onGameOver(boolean won) {
        img_basic_tower.post(new Runnable() {
            @Override
            public void run() {
                img_basic_tower.setEnabled(false);
                img_laser_tower.setEnabled(false);
                img_rocket_tower.setEnabled(false);
                btn_next_wave.setEnabled(false);
            }
        });
    }

    @Override
    public void onWaveStarted(Wave wave) {
        if (!mManager.hasWavesRemaining()) {
            btn_next_wave.post(new Runnable() {
                @Override
                public void run() {
                    btn_next_wave.setEnabled(false);
                }
            });
        }
    }

    @Override
    public void onWaveDone(Wave wave) {

    }

    @Override
    public void onCreditsChanged(final int credits) {
        if (!mManager.isGameOver()) {
            img_basic_tower.post(new Runnable() {
                @Override
                public void run() {
                    img_basic_tower.setEnabled(credits >= img_basic_tower.getItem().getValue());
                    img_laser_tower.setEnabled(credits >= img_laser_tower.getItem().getValue());
                    img_rocket_tower.setEnabled(credits >= img_rocket_tower.getItem().getValue());
                }
            });
        }
    }
}
