package ua.kpi.comsys.io8227.jackshen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class GalleryActivity extends AppCompatActivity {

    /** Handler for the array of images */
    private ArrayList<ImageView> images = new ArrayList<>();

    /** Handler for the number of current image */
    private int currentImage = 0;

    /** Handler for picking images */
    private int pickImage = 50;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        images.addAll(Arrays.asList((ImageView) findViewById(R.id.image1),
                                    (ImageView) findViewById(R.id.image2),
                                    (ImageView) findViewById(R.id.image3),
                                    (ImageView) findViewById(R.id.image4),
                                    (ImageView) findViewById(R.id.image5),
                                    (ImageView) findViewById(R.id.image6),
                                    (ImageView) findViewById(R.id.image7),
                                    (ImageView) findViewById(R.id.image8)));

        Button addImageBtn = findViewById(R.id.addImageButton);

        //Stylization button for adding images
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);
                outline.setRoundRect(0, 0, shapeSize, shapeSize, shapeSize / 2);
            }
        };
        addImageBtn.setOutlineProvider(viewOutlineProvider);
        addImageBtn.setClipToOutline(true);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, pickImage);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK && requestCode == pickImage) {
            try {
                final Uri imageUri = imageReturnedIntent.getData();
                final InputStream imageStream = getContentResolver().openInputStream(Objects.requireNonNull(imageUri));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                images.get(currentImage).setImageBitmap(selectedImage);
                currentImage = (currentImage + 1) % images.size();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
