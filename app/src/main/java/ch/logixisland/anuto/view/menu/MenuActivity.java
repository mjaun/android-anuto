package ch.logixisland.anuto.view.menu;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.theme.Theme;
import ch.logixisland.anuto.view.game.GameActivity;

import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private LevelSelectFragment levelSelectFrag = new LevelSelectFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Spinner dt = (Spinner)findViewById(R.id.darktheme_dd);
        Map<Integer,String> themes = Theme.getThemes();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, themes.values().toArray(new String[0]));
        dt.setAdapter(adapter);

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
        Spinner dt = (Spinner)findViewById(R.id.darktheme_dd);

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(LevelSelectFragment.SELECTED_LEVEL, levelId);
        intent.putExtra("theme", dt.getSelectedItemPosition());
        startActivity(intent);
    }
}
