package ch.bfh.anuto;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

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
