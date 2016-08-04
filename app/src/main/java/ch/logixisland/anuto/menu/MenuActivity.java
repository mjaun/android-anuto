package ch.logixisland.anuto.menu;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ch.logixisland.anuto.MainActivity;
import ch.logixisland.anuto.R;

public class MenuActivity extends AppCompatActivity {

    private LevelSelectFragment levelSelectFrag = new LevelSelectFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.contentPane,levelSelectFrag).commit();
    }

    public void levelClicked(View view) {

        int levelId;
        switch (view.getId()){
            case R.id.level_2:
                levelId = R.raw.level_2;
                break;
            case R.id.level_3:
                levelId = R.raw.level_3;
                break;
            case R.id.level_4:
                levelId = R.raw.level_4;
                break;
            default:
                levelId = R.raw.level_1;
        }


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LevelSelectFragment.SELECTED_LEVEL, levelId);
        startActivity(intent);
    }
}
