package ua.kpi.comsys.io8227.jackshen;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class PictureAboutActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 1;

    Picture fullPicture;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_image);

        fullPicture = (Picture) getIntent().getSerializableExtra("image_full");

        Button mDownloadButton = findViewById(R.id.action_download);

        ImageView picture = findViewById(R.id.thumbnail_view);

        if (Objects.requireNonNull(fullPicture).getImageUrl().equals("")) {
            picture.setImageResource(R.drawable.noimage);
        } else {
            new PictureAdapter.DownloadImage(picture).execute(fullPicture.getImageUrl());
        }

        ImageView authorImage = findViewById(R.id.author_img);
        if (Objects.requireNonNull(fullPicture).getUserImage().equals("")) {
            authorImage.setImageResource(R.drawable.noimage);
        } else {
            new PictureAdapter.DownloadImage(authorImage).execute(fullPicture.getUserImage());
        }

        TextView author = findViewById(R.id.txt_view_author);
        author.setText(fullPicture.getUser());

        TextView likes = findViewById(R.id.txt_view_like);
        likes.setText(fullPicture.getLikes());

        TextView favorites = findViewById(R.id.txt_view_favorite);
        favorites.setText(fullPicture.getFavorites());

        TextView downloads = findViewById(R.id.txt_view_downloads);
        String imgDownloads = "Downloads: " + fullPicture.getDownloads();
        downloads.setText(imgDownloads);

        TextView size = findViewById(R.id.txt_view_size);
        String imgSize = "Size: " + fullPicture.getImageWidth() + "x" + fullPicture.getImageHeight();
        size.setText(imgSize);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    downloadImage();
                }
            }
        });
    }

    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("To download a file it is necessary to allow required permission");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PictureAboutActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    private void downloadImage() {
        String url = fullPicture.getImageUrl();
        String name = getImageNameFromUrl(url);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(PictureAboutActivity.this, "Cannot download image", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getImageNameFromUrl(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index);
    }
}
