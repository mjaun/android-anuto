package ch.logixisland.anuto.view.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.manager.GameManager;

public class MenuActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_SELECT_LEVEL = 1;

    private final GameManager mGameManager;

    private Button btn_restart;
    private Button btn_change_level;

    public MenuActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_restart = (Button)findViewById(R.id.btn_restart);
        btn_change_level = (Button)findViewById(R.id.btn_change_level);

        btn_restart.setOnClickListener(this);
        btn_change_level.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_restart) {
            mGameManager.restart();
            finish();
        }

        if (view == btn_change_level) {
            Intent intent = new Intent(this, SelectLevelActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_LEVEL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_LEVEL) {
            finish();
        }
    }
}
