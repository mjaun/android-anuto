package ch.logixisland.anuto.view.menu;

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
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.engine.theme.ActivityType;
import ch.logixisland.anuto.view.AnutoActivity;

public class SelectLevelGridActivity extends AnutoActivity implements AdapterView.OnItemClickListener,
        ViewTreeObserver.OnScrollChangedListener {

    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;

    ImageView arrow_up;
    ImageView arrow_down;
    private GridView gridView;
    private LevelsAdapter mAdapter;

    public SelectLevelGridActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mLevelLoader = factory.getLevelLoader();
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.Menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level_grid);

        mAdapter = new LevelsAdapter(this);
        mAdapter.addLevel(R.raw.level_1, R.drawable.level_1_thumb, R.string.level_1_name);
        mAdapter.addLevel(R.raw.level_2, R.drawable.level_2_thumb, R.string.level_2_name);
        mAdapter.addLevel(R.raw.level_3, R.drawable.level_3_thumb, R.string.level_3_name);
        mAdapter.addLevel(R.raw.level_4, R.drawable.level_4_thumb, R.string.level_4_name);

        arrow_up = (ImageView) findViewById(R.id.arrow_up);
        arrow_down = (ImageView) findViewById(R.id.arrow_down);

        gridView = (GridView) findViewById(R.id.gvLevels);
        gridView.setOnItemClickListener(this);
        gridView.getViewTreeObserver().addOnScrollChangedListener(this);
        gridView.post(new Runnable(){
            @Override
            public void run() {
                updateArrowVisibility();
            }
        });
        gridView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLevelLoader.loadLevel(((LevelsAdapter.LevelItemInfo) mAdapter.getItem(position)).levelResId);
        mGameManager.restart();
        finish();
    }

        @Override
    public void onScrollChanged() {
        updateArrowVisibility();
    }

    private void updateArrowVisibility() {
//        int scrollY = gridView.getScrollY(); //gridView.getScrollY() doesn't work as I'd expect. Instead, it always gives values around 0
//        Log.d("SelectLevelGridActivity", "getScrollY(): " + scrollY);
//        arrow_up.setVisibility(scrollY < 10 ? View.INVISIBLE : View.VISIBLE);
//        arrow_down.setVisibility(scrollY > gridView.getChildAt(0).getBottom() - gridView.getHeight() - 10 ? View.INVISIBLE : View.VISIBLE);

        final int numberViews = gridView.getChildCount();
        if(numberViews <= 0){
            arrow_up.setVisibility(View.INVISIBLE);
            arrow_down.setVisibility(View.INVISIBLE);
            return;
        }

        final int firstVisibleLevel = gridView.getFirstVisiblePosition();//starting from zero!
        if(firstVisibleLevel == 0){
            arrow_up.setVisibility(gridView.getChildAt(0).getTop() < -10 ? View.VISIBLE : View.INVISIBLE);
        }else{
            arrow_up.setVisibility(firstVisibleLevel > 0 ? View.VISIBLE : View.INVISIBLE);
        }

        final int numberLevels = mAdapter.getCount();
        final int lastVisibleLevel = gridView.getLastVisiblePosition();
        if(lastVisibleLevel == numberLevels - 1){
            arrow_down.setVisibility(gridView.getChildAt(numberViews - 1).getBottom() > gridView.getHeight() + 10 ? View.VISIBLE : View.INVISIBLE);
        }else{
            arrow_down.setVisibility(lastVisibleLevel < numberLevels - 1 ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
