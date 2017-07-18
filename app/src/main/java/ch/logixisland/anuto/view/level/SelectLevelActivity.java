package ch.logixisland.anuto.view.level;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.level.LevelRepository;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.HighScores;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;

public class SelectLevelActivity extends AnutoActivity implements AdapterView.OnItemClickListener,
        ViewTreeObserver.OnScrollChangedListener {

    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;
    private final LevelRepository mLevelRepository;
    private final HighScores mHighScores;

    private LevelsAdapter mAdapter;

    private ImageView arrow_up;
    private ImageView arrow_down;
    private GridView grid_levels;

    public SelectLevelActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mLevelLoader = factory.getLevelLoader();
        mLevelRepository = factory.getLevelRepository();
        mHighScores = factory.getHighScores();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        mAdapter = new LevelsAdapter(this, mLevelRepository, mHighScores);

        arrow_up = (ImageView) findViewById(R.id.arrow_up);
        arrow_down = (ImageView) findViewById(R.id.arrow_down);

        grid_levels = (GridView) findViewById(R.id.grid_levels);
        grid_levels.setOnItemClickListener(this);
        grid_levels.getViewTreeObserver().addOnScrollChangedListener(this);
        grid_levels.post(new Runnable() {
            @Override
            public void run() {
                updateArrowVisibility();
            }
        });
        grid_levels.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLevelLoader.loadLevel(mLevelRepository.getLevels().get(position));
        mGameManager.restart();
        finish();
    }

    @Override
    public void onScrollChanged() {
        updateArrowVisibility();
    }

    private void updateArrowVisibility() {
        final int numberViews = grid_levels.getChildCount();
        if (numberViews <= 0) {
            arrow_up.setVisibility(View.INVISIBLE);
            arrow_down.setVisibility(View.INVISIBLE);
            return;
        }

        final int firstVisibleLevel = grid_levels.getFirstVisiblePosition();
        if (firstVisibleLevel == 0) {
            arrow_up.setVisibility(grid_levels.getChildAt(0).getTop() < -10 ? View.VISIBLE : View.INVISIBLE);
        } else {
            arrow_up.setVisibility(firstVisibleLevel > 0 ? View.VISIBLE : View.INVISIBLE);
        }

        final int numberLevels = mAdapter.getCount();
        final int lastVisibleLevel = grid_levels.getLastVisiblePosition();
        if (lastVisibleLevel == numberLevels - 1) {
            arrow_down.setVisibility(grid_levels.getChildAt(numberViews - 1).getBottom() > grid_levels.getHeight() + 10 ? View.VISIBLE : View.INVISIBLE);
        } else {
            arrow_down.setVisibility(lastVisibleLevel < numberLevels - 1 ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
