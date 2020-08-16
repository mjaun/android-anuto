package ch.logixisland.anuto.view.stats;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.entity.EntityTypes;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.EnemyProperties;
import ch.logixisland.anuto.entity.enemy.EnemyType;

public class EnemiesAdapter extends BaseAdapter {
    private static String sTheme;
    private static Map<EnemyType, Bitmap> sEnemyCache;

    private final List<Enemy> mEnemies;

    private final WeakReference<Activity> mActivityRef;
    private Theme mTheme;

    public EnemiesAdapter(Activity activity, Theme theme, EntityRegistry entityRegistry) {
        mActivityRef = new WeakReference<>(activity);
        mTheme = theme;

        if (!mTheme.getName().equals(sTheme) || sEnemyCache == null) {
            sTheme = mTheme.getName();
            sEnemyCache = new HashMap<>();
        }

        mEnemies = new ArrayList<>();
        for (String name : entityRegistry.getEntityNamesByType(EntityTypes.ENEMY)) {
            mEnemies.add((Enemy) entityRegistry.createEntity(name));
        }
    }

    static private class ViewHolder {
        ImageView img_enemy;
        TextView txt_name;
        TextView txt_health;
        TextView txt_speed;
        TextView txt_reward;
        TextView txt_weak_against;
        TextView txt_strong_against;

        ViewHolder(View view) {
            img_enemy = view.findViewById(R.id.img_enemy);
            txt_name = view.findViewById(R.id.txt_name);
            txt_health = view.findViewById(R.id.txt_health);
            txt_speed = view.findViewById(R.id.txt_speed);
            txt_reward = view.findViewById(R.id.txt_reward);
            txt_weak_against = view.findViewById(R.id.txt_weak_against);
            txt_strong_against = view.findViewById(R.id.txt_strong_against);
        }
    }

    @Override
    public int getCount() {
        return mEnemies.size();
    }

    @Override
    public Object getItem(int position) {
        return mEnemies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private Bitmap createPreviewBitmap(Enemy enemy) {
        Viewport viewport = new Viewport();
        viewport.setGameSize(1, 1);
        viewport.setScreenSize(120, 120);

        Bitmap bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.concat(viewport.getScreenMatrix());
        enemy.drawPreview(canvas);

        return bitmap;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = mActivityRef.get();

        if (activity == null) {
            return convertView;
        }

        View enemyItemView;

        if (convertView == null) {
            enemyItemView = LayoutInflater.from(activity).inflate(R.layout.item_enemy, parent, false);
        } else {
            enemyItemView = convertView;
        }

        Enemy enemy = mEnemies.get(position);
        EnemyProperties enemyProperties = enemy.getEnemyProperties();

        ViewHolder viewHolder = new ViewHolder(enemyItemView);

        String tmp = activity.getString(enemy.getTextId());
        viewHolder.txt_name.setText(tmp);

        DecimalFormat fmt = new DecimalFormat();
        tmp = fmt.format(enemyProperties.getHealth());
        viewHolder.txt_health.setText(tmp);

        DecimalFormat fmt2 = new DecimalFormat("#0 '%'");
        tmp = fmt2.format(enemyProperties.getSpeed() * 100);
        viewHolder.txt_speed.setText(tmp);

        tmp = fmt.format(enemyProperties.getReward());
        viewHolder.txt_reward.setText(tmp);

        tmp = TextUtils.join("\n", enemyProperties.getWeakAgainst());
        viewHolder.txt_weak_against.setText(tmp.length() > 0 ? tmp : activity.getString(R.string.none));
        viewHolder.txt_weak_against.setTextColor(mTheme.getColor(R.attr.weakAgainstColor));


        tmp = TextUtils.join("\n", enemyProperties.getStrongAgainst());
        viewHolder.txt_strong_against.setText(tmp.length() > 0 ? tmp : activity.getString(R.string.none));
        viewHolder.txt_strong_against.setTextColor(mTheme.getColor(R.attr.strongAgainstColor));

        if (!sEnemyCache.containsKey(enemyProperties.getEnemyType())) {
            Bitmap bmp = createPreviewBitmap(enemy);
            sEnemyCache.put(enemyProperties.getEnemyType(), bmp);
        }

        viewHolder.img_enemy.setImageBitmap(sEnemyCache.get(enemyProperties.getEnemyType()));

        return enemyItemView;
    }

}
