package ch.logixisland.anuto.view.map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.ImageView;

class LoadThumbTask extends AsyncTask<Void, Void, Bitmap> {

    private static final SparseArray<Bitmap> sThumbCache = new SparseArray<>();
    private static final MapThumbGenerator sMapThumbGenerator = new MapThumbGenerator();

    private final Resources mResources;
    private final ImageView mImageView;
    private final int mMapResId;

    LoadThumbTask(Resources resources, ImageView imageView, int mapResId) {
        mResources = resources;
        mImageView = imageView;
        mMapResId = mapResId;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap thumb = sThumbCache.get(mMapResId);

        if (thumb == null) {
            thumb = sMapThumbGenerator.generateThumb(mResources, mMapResId);
            sThumbCache.append(mMapResId, thumb);
        }

        return thumb;
    }

    @Override
    protected void onPostExecute(Bitmap thumb) {
        mImageView.setImageBitmap(thumb);
    }
}
