package ch.bfh.anuto;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {

    TowerDefenseView tdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tdView = (TowerDefenseView)findViewById(R.id.td_view);
    }

    @Override
    public void onStart() {
        super.onStart();
        tdView.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        tdView.stop();
    }
}
