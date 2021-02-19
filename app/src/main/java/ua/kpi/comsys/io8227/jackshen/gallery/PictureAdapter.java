package ua.kpi.comsys.io8227.jackshen.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import ua.kpi.comsys.io8227.jackshen.R;

public class PictureAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private Context mContext;
    private List<Picture> mImageList;

    PictureAdapter(Context context, List<Picture> imageList) {
        mContext = context;
        mImageList = imageList;
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, null);
        return new PictureViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, final int position) {
        Picture currentImage = mImageList.get(position);

        Picasso.get().load(currentImage.getImageUrl()).resize(300, 300).centerCrop().into(holder.mImageView);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PictureAboutActivity.class);
                intent.putExtra("image_full",  mImageList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
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

