package ch.logixisland.anuto.view.map;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameLoader;
import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.HighScores;
import ch.logixisland.anuto.business.game.MapRepository;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;

public class ChangeMapActivity extends AnutoActivity implements AdapterView.OnItemClickListener,
        ViewTreeObserver.OnScrollChangedListener {

    private final GameState mGameState;
    private final GameLoader mGameLoader;
    private final MapRepository mMapRepository;
    private final HighScores mHighScores;

    private MapsAdapter mAdapter;

    private ImageView arrow_up;
    private ImageView arrow_down;
    private GridView grid_maps;

    public ChangeMapActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameState = factory.getGameState();
        mGameLoader = factory.getGameLoader();
        mMapRepository = factory.getMapRepository();
        mHighScores = factory.getHighScores();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_map);

        mAdapter = new MapsAdapter(this, mMapRepository, mHighScores);

        arrow_up = (ImageView) findViewById(R.id.arrow_up);
        arrow_down = (ImageView) findViewById(R.id.arrow_down);

        grid_maps = (GridView) findViewById(R.id.grid_maps);
        grid_maps.setOnItemClickListener(this);
        grid_maps.getViewTreeObserver().addOnScrollChangedListener(this);
        grid_maps.post(new Runnable() {
            @Override
            public void run() {
                updateArrowVisibility();
            }
        });
        grid_maps.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mGameLoader.loadMap(mMapRepository.getMaps().get(position));
        mGameState.restart();
        finish();
    }

    @Override
    public void onScrollChanged() {
        updateArrowVisibility();
    }

    private void updateArrowVisibility() {
        if (grid_maps.getChildCount() <= 0) {
            arrow_up.setVisibility(View.INVISIBLE);
            arrow_down.setVisibility(View.INVISIBLE);
            return;
        }

        if (grid_maps.getFirstVisiblePosition() == 0) {
            arrow_up.setVisibility(grid_maps.getChildAt(0).getTop() < -10 ? View.VISIBLE : View.INVISIBLE);
        } else {
            arrow_up.setVisibility(grid_maps.getFirstVisiblePosition() > 0 ? View.VISIBLE : View.INVISIBLE);
        }

        if (grid_maps.getLastVisiblePosition() == mAdapter.getCount() - 1) {
            arrow_down.setVisibility(grid_maps.getChildAt(grid_maps.getChildCount() - 1).getBottom() > grid_maps.getHeight() + 10 ? View.VISIBLE : View.INVISIBLE);
        } else {
            arrow_down.setVisibility(grid_maps.getLastVisiblePosition() < mAdapter.getCount() - 1 ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
