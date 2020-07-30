package ch.logixisland.anuto.view.loadmenu;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.game.SaveGameInfo;
import ch.logixisland.anuto.business.game.SaveGameRepository;
import ch.logixisland.anuto.util.StringUtils;

public class SaveGamesAdapter extends BaseAdapter {

    private static final Map<String, Bitmap> sThumbCache = new HashMap<>();

    private final WeakReference<Activity> mActivityRef;
    private final List<SaveGameInfo> mSaveGameInfos;

    SaveGamesAdapter(Activity activity, SaveGameRepository saveGameRepository) {
        mActivityRef = new WeakReference<>(activity);
        mSaveGameInfos = saveGameRepository.getSavegameInfos();
    }

    static private class ViewHolder {
        ImageView img_thumb;
        TextView txt_datetime;
        TextView txt_score;
        TextView txt_waveNumber;
        TextView txt_lives;

        ViewHolder(View view) {
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_datetime = view.findViewById(R.id.txt_datetime);
            txt_score = view.findViewById(R.id.txt_score);
            txt_waveNumber = view.findViewById(R.id.txt_waveNumber);
            txt_lives = view.findViewById(R.id.txt_lives);
        }
    }

    @Override
    public int getCount() {
        return mSaveGameInfos.size();
    }

    @Override
    public SaveGameInfo getItem(int position) {
        return mSaveGameInfos.get(position);
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

        View sgItemView;

        if (convertView == null) {
            sgItemView = LayoutInflater.from(activity).inflate(R.layout.item_savegame, parent, false);
        } else {
            sgItemView = convertView;
        }

        Resources resources = activity.getResources();
        SaveGameInfo saveGameInfo = mSaveGameInfos.get(position);
        ViewHolder viewHolder = new ViewHolder(sgItemView);

        viewHolder.txt_datetime.setText(saveGameInfo.getDatetime());
        String tmp = resources.getString(R.string.score) + ": " + StringUtils.formatSuffix(saveGameInfo.getScore());
        viewHolder.txt_score.setText(tmp);
        tmp = resources.getString(R.string.wave) + ": " + StringUtils.formatSuffix(saveGameInfo.getWave());
        viewHolder.txt_waveNumber.setText(tmp);
        tmp = resources.getString(R.string.lives) + ": " + StringUtils.formatSuffix(saveGameInfo.getLives());
        viewHolder.txt_lives.setText(tmp);

        viewHolder.img_thumb.setImageBitmap(saveGameInfo.getCachedScreenshot());

        return sgItemView;
    }
}
