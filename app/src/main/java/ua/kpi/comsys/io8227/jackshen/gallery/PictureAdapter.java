package ua.kpi.comsys.io8227.jackshen.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final PictureViewHolder holder, final int position) {
        Picture currentImage = mImageList.get(position);

        // Picture data
        String imageID = currentImage.getImageID();
        String imageURL = currentImage.getImageUrl();
        String imageTags = currentImage.getImageTags();
        String imageWidth = currentImage.getImageWidth();
        String imageHeight = currentImage.getImageHeight();
        String imageViews = currentImage.getViews();
        String imageDownloads = currentImage.getDownloads();
        String imageFavorites = currentImage.getFavorites();
        String imageLikes = currentImage.getLikes();
        String userName = currentImage.getUser();

        if (PictureActivity.isNetworkConnected(mContext)) {
            String imageTitle = imageURL.substring(imageURL.lastIndexOf("/") + 1);

            Picasso.get()
                    .load(imageURL)
                    .resize(300, 300)
                    .centerCrop()
                    .into(picassoImageTarget(
                            mContext,
                            "images",
                            imageTitle,
                            imageID
                    ));

            Picasso.get()
                    .load(imageURL)
                    .resize(300, 300)
                    .centerCrop()
                    .into(holder.mImageView);
        } else {
            // Load image from local storage if network unavailable
            if (imageURL == null || imageURL.equals("")){
                Toast.makeText(mContext, "Error! Image is null -> cannot load image from storage", Toast.LENGTH_SHORT).show();
            } else {
                Picasso.get()
                        .load(new File(imageURL))
                        .into(holder.mImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(mContext, "Cannot load image from storage", Toast.LENGTH_SHORT).show();
                                holder.mImageView.setImageResource(R.drawable.noimage);
                            }
                        });
            }
        }

        if (PictureActivity.isNetworkConnected(mContext)) {
            // Check if current Picture object exist in our DB, if not -> add
            if (!PictureProvider.сheckIfExist(imageID)) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(PictureContract.PictureEntry.IMAGE_NET_ID, imageID);
                contentValues.put(PictureContract.PictureEntry.IMAGE_TAGS, imageTags);
                contentValues.put(PictureContract.PictureEntry.IMAGE_WIDTH, imageWidth);
                contentValues.put(PictureContract.PictureEntry.IMAGE_HEIGHT, imageHeight);
                contentValues.put(PictureContract.PictureEntry.IMAGE_VIEWS, imageViews);
                contentValues.put(PictureContract.PictureEntry.IMAGE_DOWNLOADS, imageDownloads);
                contentValues.put(PictureContract.PictureEntry.IMAGE_FAVORITES, imageFavorites);
                contentValues.put(PictureContract.PictureEntry.IMAGE_LIKES, imageLikes);
                contentValues.put(PictureContract.PictureEntry.USER_NAME, userName);

                mContext.getContentResolver().insert(PictureContract.PictureEntry.CONTENT_URI, contentValues);

                Log.i("picture", "new Picture object add to DB: " + imageID);
            }
        }


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

    private Target picassoImageTarget(final Context context, final String imageDir,
                                      final String imageName, final String imageID) {

        ContextWrapper cw = new ContextWrapper(context);

        // path to /data/data/this_app/app_imageDir
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE);
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file

                        // Check if current image is already in phone storage, if not -> save it
                        if (!myImageFile.exists()) {
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(myImageFile);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            ContentValues values = new ContentValues();
                            values.put(PictureContract.PictureEntry.IMAGE_URL, myImageFile.getAbsolutePath());

                            // add image path to our DB
                            context.getContentResolver().update(
                                    PictureContract.PictureEntry.CONTENT_URI,
                                    values,
                                    "image_net_id = ?",
                                    new String[]{imageID});

                            Log.i("image", "image saved to >>> " + myImageFile.getAbsolutePath());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };
    }

}

