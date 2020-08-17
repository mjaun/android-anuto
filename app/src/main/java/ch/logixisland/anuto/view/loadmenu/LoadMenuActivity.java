package ch.logixisland.anuto.view.loadmenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.GameLoader;
import ch.logixisland.anuto.business.game.SaveGameRepository;
import ch.logixisland.anuto.view.AnutoActivity;

import static android.content.ContentValues.TAG;

public class LoadMenuActivity extends AnutoActivity implements AdapterView.OnItemClickListener,
        ViewTreeObserver.OnScrollChangedListener {

    public static int CONTEXT_MENU__DELETE_ID = 0;

    private final GameLoader mGameLoader;
    private final SaveGameRepository mSaveGameRepository;

    private SaveGamesAdapter mAdapter;

    private ImageView arrow_up;
    private ImageView arrow_down;
    private GridView grid_savegames;


    public LoadMenuActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameLoader = factory.getGameLoader();
        mSaveGameRepository = factory.getSaveGameRepository();
    }

    @Override
    protected ch.logixisland.anuto.engine.theme.ActivityType getActivityType() {
        return ch.logixisland.anuto.engine.theme.ActivityType.Normal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load_menu);

        mSaveGameRepository.refresh(mGameLoader);
        mAdapter = new SaveGamesAdapter(this, mSaveGameRepository);

        arrow_up = findViewById(R.id.arrow_up);
        arrow_down = findViewById(R.id.arrow_down);

        grid_savegames = findViewById(R.id.grid_savegames);
        grid_savegames.setOnItemClickListener(this);
        grid_savegames.getViewTreeObserver().addOnScrollChangedListener(this);
        grid_savegames.post(new Runnable() {
            @Override
            public void run() {
                updateArrowVisibility();
            }
        });
        grid_savegames.setAdapter(mAdapter);
        registerForContextMenu(grid_savegames);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mGameLoader.loadGame(mAdapter.getItem(position).getSaveGamePath());

        finish();
    }

    public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CONTEXT_MENU__DELETE_ID, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == CONTEXT_MENU__DELETE_ID) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Log.d(TAG, "Item Timestamp at POSITION:" + mAdapter.getItem(info.position).getDatetime());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.deleteConfirmation)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            mSaveGameRepository.removeSGIAt(info.position);
                            mAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return false;
    }

    @Override
    public void onScrollChanged() {
        updateArrowVisibility();
    }

    private void updateArrowVisibility() {
        if (grid_savegames.getChildCount() <= 0) {
            arrow_up.setVisibility(View.INVISIBLE);
            arrow_down.setVisibility(View.INVISIBLE);
            return;
        }

        if (grid_savegames.getFirstVisiblePosition() == 0) {
            arrow_up.setVisibility(grid_savegames.getChildAt(0).getTop() < -10 ? View.VISIBLE : View.INVISIBLE);
        } else {
            arrow_up.setVisibility(grid_savegames.getFirstVisiblePosition() > 0 ? View.VISIBLE : View.INVISIBLE);
        }

        if (grid_savegames.getLastVisiblePosition() == mAdapter.getCount() - 1) {
            arrow_down.setVisibility(grid_savegames.getChildAt(grid_savegames.getChildCount() - 1).getBottom() > grid_savegames.getHeight() + 10 ? View.VISIBLE : View.INVISIBLE);
        } else {
            arrow_down.setVisibility(grid_savegames.getLastVisiblePosition() < mAdapter.getCount() - 1 ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
