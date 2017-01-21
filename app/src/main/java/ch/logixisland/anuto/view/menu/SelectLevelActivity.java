package ch.logixisland.anuto.view.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.manager.GameManager;

public class SelectLevelActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private final GameManager mGameManager;
    private final LevelLoader mLevelLoader;

    private Map<ImageButton, Integer> mLevelButtons = new HashMap<>();

    HorizontalScrollView scroll_view;
    ImageView arrow_left;
    ImageView arrow_right;

    public SelectLevelActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameManager = factory.getGameManager();
        mLevelLoader = factory.getLevelLoader();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        setupLevelButton(R.id.btn_level_1, R.raw.level_1);
        setupLevelButton(R.id.btn_level_2, R.raw.level_2);
        setupLevelButton(R.id.btn_level_3, R.raw.level_3);
        setupLevelButton(R.id.btn_level_4, R.raw.level_4);

        scroll_view = (HorizontalScrollView) findViewById(R.id.scroll_view);
        arrow_left = (ImageView) findViewById(R.id.arrow_left);
        arrow_right = (ImageView) findViewById(R.id.arrow_right);

        scroll_view.setOnTouchListener(this);
        scroll_view.post(new Runnable() {
            @Override
            public void run() {
                arrow_left.setVisibility(scroll_view.getScrollX() == 0 ? View.INVISIBLE : View.VISIBLE);
                arrow_right.setVisibility(scroll_view.getScrollX() == scroll_view.getChildAt(0).getRight() - scroll_view.getWidth() ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageButton && mLevelButtons.containsKey(view)) {
            mLevelLoader.loadLevel(mLevelButtons.get(view));
            mGameManager.restart();
            finish();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            arrow_left.setVisibility(scroll_view.getScrollX() == 0 ? View.INVISIBLE : View.VISIBLE);
            arrow_right.setVisibility(scroll_view.getScrollX() == scroll_view.getChildAt(0).getRight() - scroll_view.getWidth() ? View.INVISIBLE : View.VISIBLE);
        }
        return false;
    }

    private void setupLevelButton(int buttonId, int levelId) {
        ImageButton button = (ImageButton) findViewById(buttonId);
        mLevelButtons.put(button, levelId);
        button.setOnClickListener(this);
    }
}
