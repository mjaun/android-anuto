package ch.logixisland.anuto.view.map;

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
import ch.logixisland.anuto.business.game.HighScores;
import ch.logixisland.anuto.data.map.MapInfo;
import ch.logixisland.anuto.data.map.MapRepository;

class MapsAdapter extends BaseAdapter {

    private final WeakReference<Activity> mActivityRef;
    private final HighScores mHighScores;
    private final List<MapInfo> mMapInfos;

    MapsAdapter(Activity activity, MapRepository mapRepository, HighScores highScores) {
        mActivityRef = new WeakReference<>(activity);
        mMapInfos = mapRepository.getMaps();
        mHighScores = highScores;
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
        return mMapInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mMapInfos.get(position);
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

        View mapItemView;

        if (convertView == null) {
            mapItemView = LayoutInflater.from(activity).inflate(R.layout.item_map, parent, false);
        } else {
            mapItemView = convertView;
        }

        Resources resources = activity.getResources();
        MapInfo mapInfo = mMapInfos.get(position);
        ViewHolder viewHolder = new ViewHolder(mapItemView);

        viewHolder.txt_name.setText(resources.getString(mapInfo.getMapNameResId()));

        DecimalFormat fmt = new DecimalFormat("###,###,###,###");
        String highScore = fmt.format(mHighScores.getHighScore(mapInfo.getMapId()));
        viewHolder.txt_highscore.setText(resources.getString(R.string.score) + ": " + highScore);

        viewHolder.img_thumb.setImageBitmap(null);
        new LoadThumbTask(resources, viewHolder.img_thumb, mapInfo.getMapDescriptorResId()).execute();

        return mapItemView;
    }
}
