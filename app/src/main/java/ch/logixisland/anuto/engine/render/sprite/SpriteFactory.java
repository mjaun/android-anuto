package ch.logixisland.anuto.engine.render.sprite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ch.logixisland.anuto.engine.theme.ThemeManager;

public class SpriteFactory {

    private final Context mContext;
    private final ThemeManager mThemeManager;

    public SpriteFactory(Context context, ThemeManager themeManager) {
        mContext = context;
        mThemeManager = themeManager;
    }

    public SpriteTemplate createTemplate(int attrId, int spriteCount) {
        int resourceId = mThemeManager.getTheme().getResourceId(attrId);

        Bitmap sheet = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
        Bitmap[] sprites = new Bitmap[spriteCount];
        int spriteWidth = sheet.getWidth() / spriteCount;
        int spriteHeight = sheet.getHeight();

        for (int i = 0; i < spriteCount; i++) {
            sprites[i] = Bitmap.createBitmap(sheet, spriteWidth * i, 0, spriteWidth, spriteHeight);
        }

        return new SpriteTemplate(sprites);
    }

    public StaticSprite createStatic(int layer, SpriteTemplate template) {
        return new StaticSprite(layer, template);
    }

    public AnimatedSprite createAnimated(int layer, SpriteTemplate template) {
        return new AnimatedSprite(layer, template);
    }

    public ReplicatedSprite createReplication(SpriteInstance original) {
        return new ReplicatedSprite(original);
    }

}
