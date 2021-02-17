package ua.kpi.comsys.io8227.jackshen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class PictureAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private Context mContext;
    private List<Picture> mImageList;
    private List<Drawable> mImages;

    PictureAdapter(Context context, List<Picture> imageList, List<Drawable> images) {
        mContext = context;
        mImageList = imageList;
        mImages = images;
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, null);
        return new PictureViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, final int position) {
        holder.mImageView.setImageDrawable(mImages.get(position));
        if (position % 8 == 1) {
            holder.mImageView.getLayoutParams().height = (int) mContext.getResources().getDimension(R.dimen.imageview_height);
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.mImageView.requestLayout();

        }
//        Picture currentImage = getItem(position);
//        new DownloadImage(holder.mImageView).execute(currentImage.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public Picture getItem(int position) {
        return mImageList.get(position);
    }


    /** Class to download an image from URL */
    public static class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;

        DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

