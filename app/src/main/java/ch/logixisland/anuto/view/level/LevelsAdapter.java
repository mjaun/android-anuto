package ch.logixisland.anuto.view.level;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.level.LevelInfo;
import ch.logixisland.anuto.business.level.LevelRepository;
import ch.logixisland.anuto.business.score.HighScoreBoard;

class LevelsAdapter extends BaseAdapter {

    private final WeakReference<Activity> mActivityRef;
    private final HighScoreBoard mHighScoreBoard;
    private final List<LevelInfo> mLevelInfos;

    LevelsAdapter(Activity activity, LevelRepository levelRepository, HighScoreBoard highScoreBoard) {
        mActivityRef = new WeakReference<>(activity);
        mLevelInfos = levelRepository.getLevels();
        mHighScoreBoard = highScoreBoard;
    }

    static private class ViewHolder {
        ImageView img_thumb;
        TextView txt_name;
        TextView txt_highscore;

        ViewHolder(View view) {
            img_thumb = (ImageView) view.findViewById(R.id.img_thumb);
            txt_name = (TextView) view.findViewById(R.id.txt_name);
            txt_highscore = (TextView) view.findViewById(R.id.txt_highscore);
        }
    }

    @Override
    public int getCount() {
        return mLevelInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mLevelInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = mActivityRef.get();

        if (activity == null) {
            return convertView;
        }

        View levelItemView;

        if (convertView == null) {
            levelItemView = LayoutInflater.from(activity).inflate(R.layout.item_level_select, parent, false);
        } else {
            levelItemView = convertView;
        }

        Resources resources = activity.getResources();
        LevelInfo levelInfo = mLevelInfos.get(position);
        ViewHolder viewHolder = new ViewHolder(levelItemView);

        DecimalFormat fmt = new DecimalFormat("###,###,###,###");
        String highScore = fmt.format(mHighScoreBoard.getHighScore(levelInfo.getLevelId()));

        viewHolder.img_thumb.setImageResource(levelInfo.getLevelThumbId());
        viewHolder.txt_name.setText(resources.getString(levelInfo.getLevelNameId()));
        viewHolder.txt_highscore.setText(resources.getString(R.string.score) + ": " + highScore);

        return levelItemView;
    }
}
