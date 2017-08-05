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
    private final int mMapDescriptorResId;

    LoadThumbTask(Resources resources, ImageView imageView, int mapDescriptorResId) {
        mResources = resources;
        mImageView = imageView;
        mMapDescriptorResId = mapDescriptorResId;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap thumb = sThumbCache.get(mMapDescriptorResId);

        if (thumb == null) {
            thumb = sMapThumbGenerator.generateThumb(mResources, mMapDescriptorResId);
            sThumbCache.append(mMapDescriptorResId, thumb);
        }

        return thumb;
    }

    @Override
    protected void onPostExecute(Bitmap thumb) {
        mImageView.setImageBitmap(thumb);
    }
}
