package ch.logixisland.anuto.view.menu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;

class LevelsAdapter extends BaseAdapter {

    private WeakReference<Activity> activity;
    private List<LevelItemInfo> levelItems;

    LevelsAdapter(Activity activity) {
        this.activity = new WeakReference<Activity>(activity);
        this.levelItems = new ArrayList<>();
    }

    void addLevel(int level, int thumb, int name) {
        //beware the order of ints
        levelItems.add(new LevelItemInfo(level, thumb, name));
    }

    static class LevelItemInfo {
        int levelResId;
        int thumbResId;
        int nameResId;

        public LevelItemInfo(int levelResId, int thumbResId, int nameResId) {
            this.levelResId = levelResId;
            this.thumbResId = thumbResId;
            this.nameResId = nameResId;
        }
    }

    static private class ViewHolder {
        ImageView ibThumb;
        TextView tvIndex, tvName;

        ViewHolder(View view) {
            ibThumb = (ImageView) view.findViewById(R.id.levelThumb);
            tvIndex = (TextView) view.findViewById(R.id.levelIndex);
            tvName = (TextView) view.findViewById(R.id.levelName);
        }
    }

    @Override
    public int getCount() {
        return levelItems.size();
    }

    @Override
    public Object getItem(int position) {
        return levelItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity act = activity.get();
        if (act == null) {
            return convertView;
        }

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(act).inflate(R.layout.item_level_select, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LevelItemInfo itemInfo = (LevelItemInfo) getItem(position);
        if (itemInfo == null) {
            return convertView;
        }

        viewHolder.ibThumb.setImageResource(itemInfo.thumbResId);
        viewHolder.tvIndex.setText(String.valueOf(position));
        viewHolder.tvName.setText(act.getResources().getString(itemInfo.nameResId));

        return convertView;
    }
}
