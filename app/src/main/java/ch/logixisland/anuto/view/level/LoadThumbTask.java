package ch.logixisland.anuto.view.level;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.ImageView;

class LoadThumbTask extends AsyncTask<Void, Void, Bitmap> {

    private static final SparseArray<Bitmap> sThumbCache = new SparseArray<>();
    private static final LevelThumbGenerator sLevelThumbGenerator = new LevelThumbGenerator();

    private final Resources mResources;
    private final ImageView mImageView;
    private final int mLevelDataResId;

    LoadThumbTask(Resources resources, ImageView imageView, int levelDataResId) {
        mResources = resources;
        mImageView = imageView;
        mLevelDataResId = levelDataResId;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap thumb = sThumbCache.get(mLevelDataResId);

        if (thumb == null) {
            thumb = sLevelThumbGenerator.generateThumb(mResources, mLevelDataResId);
            sThumbCache.append(mLevelDataResId, thumb);
        }

        return thumb;
    }

    @Override
    protected void onPostExecute(Bitmap thumb) {
        mImageView.setImageBitmap(thumb);
    }
}
