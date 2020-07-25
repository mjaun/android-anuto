package ch.logixisland.anuto.view.faq;

import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.view.AnutoActivity;

public class FAQActivity extends AnutoActivity implements ThemeManager.Listener {

    private EnemiesAdapter mAdapter;

    private GridView grid_enemies;
    private Context appContext;
    private Theme mTheme;

    public FAQActivity() {
        AnutoApplication app = AnutoApplication.getInstance();
        appContext = app.getApplicationContext();
        mTheme = app.getGameFactory().getGameEngine().getThemeManager().getTheme();

    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        mAdapter = new EnemiesAdapter(this, appContext, mTheme);

        grid_enemies = findViewById(R.id.grid_enemies);
        grid_enemies.setAdapter(mAdapter);
    }
}
