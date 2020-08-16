package ch.logixisland.anuto.view.stats;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.entity.enemy.EnemyProperties;
import ch.logixisland.anuto.entity.enemy.EnemyType;

public class EnemiesAdapter extends BaseAdapter {
    private static String sTheme;
    private static Map<EnemyType, Bitmap> sEnemyCache;

    private final List<EnemyProperties> mEnemyProperties;

    private final WeakReference<Activity> mActivityRef;
    private Context appContext;
    private Theme mTheme;

    public EnemiesAdapter(Activity activity, Context context, Theme theme) {
        mActivityRef = new WeakReference<>(activity);
        appContext = context;
        mTheme = theme;

        if ((sTheme != theme.getName()) || (sEnemyCache == null)) {
            sTheme = theme.getName();
            sEnemyCache = new HashMap<>();
        }

        mEnemyProperties = new ArrayList<>();
        for (EnemyType x : EnemyType.values()) {
            mEnemyProperties.add(new EnemyProperties.Builder(x.name()).build());
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
        return mEnemyProperties.size();
    }

    @Override
    public Object getItem(int position) {
        return mEnemyProperties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private Bitmap extractSingleBmp(EnemyType enemyType) {
        int attrId, spriteCount, spriteId;

        switch (enemyType) {
            case soldier:
                attrId = R.attr.soldier;
                spriteCount = 12;
                spriteId = 0;
                break;
            case blob:
                attrId = R.attr.blob;
                spriteCount = 9;
                spriteId = 0;
                break;
            case sprinter:
                attrId = R.attr.sprinter;
                spriteCount = 6;
                spriteId = 3;
                break;
            case flyer:
                attrId = R.attr.flyer;
                spriteCount = 6;
                spriteId = 4;
                break;
            case healer:
                attrId = R.attr.healer;
                spriteCount = 4;
                spriteId = 0;
                break;
            default:
                throw new RuntimeException("Unknown enemy!");
        }

        Bitmap sheet = BitmapFactory.decodeResource(appContext.getResources(), mTheme.getResourceId(attrId));
        int spriteWidth = sheet.getWidth() / spriteCount;
        int spriteHeight = sheet.getHeight();


        float aspect = (float) spriteWidth / spriteHeight;

        float newHeight = 120;
        float newWidth = newHeight * aspect;
        float scaleHeight = (newHeight) / spriteHeight;
        float scaleWidth = (newWidth) / spriteWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(sheet, spriteWidth * spriteId, 0, spriteWidth, spriteHeight, matrix, false);
        sheet.recycle();
        return resizedBitmap;
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
        EnemyProperties enemyProperties = mEnemyProperties.get(position);
        ViewHolder viewHolder = new ViewHolder(enemyItemView);
        String dmp;

        switch (enemyProperties.getEnemyType()) {
            case soldier:
                dmp = activity.getString(R.string.soldier);
                break;
            case blob:
                dmp = activity.getString(R.string.blob);
                break;
            case sprinter:
                dmp = activity.getString(R.string.sprinter);
                break;
            case flyer:
                dmp = activity.getString(R.string.flyer);
                break;
            case healer:
                dmp = activity.getString(R.string.healer);
                break;
            default:
                throw new RuntimeException("Unknown enemy!");
        }
        viewHolder.txt_name.setText(dmp);

        DecimalFormat fmt = new DecimalFormat();
        dmp = fmt.format(enemyProperties.getHealth());
        viewHolder.txt_health.setText(dmp);

        DecimalFormat df2 = new DecimalFormat("#0 '%'");
        dmp = df2.format(enemyProperties.getSpeed() * 100);
        viewHolder.txt_speed.setText(dmp);

        dmp = fmt.format(enemyProperties.getReward());
        viewHolder.txt_reward.setText(dmp);

        dmp = TextUtils.join("\n", enemyProperties.getWeakAgainst());
        viewHolder.txt_weak_against.setText(dmp.length() > 0 ? dmp : activity.getString(R.string.none));
        viewHolder.txt_weak_against.setTextColor(mTheme.getColor(R.attr.weakAgainstColor));


        dmp = TextUtils.join("\n", enemyProperties.getStrongAgainst());
        viewHolder.txt_strong_against.setText(dmp.length() > 0 ? dmp : activity.getString(R.string.none));
        viewHolder.txt_strong_against.setTextColor(mTheme.getColor(R.attr.strongAgainstColor));


        if (!sEnemyCache.containsKey(enemyProperties.getEnemyType())) {
            Bitmap bmp = extractSingleBmp(enemyProperties.getEnemyType());
            sEnemyCache.put(enemyProperties.getEnemyType(), bmp);
        }

        viewHolder.img_enemy.setImageBitmap(sEnemyCache.get(enemyProperties.getEnemyType()));

        return enemyItemView;
    }

}
