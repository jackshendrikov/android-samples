package ua.kpi.comsys.io8227.jackshen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView, startText;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        startText = findViewById(R.id.textViewStart);

        imageView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.dark);

                    startText.setText("Good ");
                    textView.setText("Night");


                    textView.setTextSize(26);
                    startText.setTextSize(26);

                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.light);

                    startText.setText("Good ");
                    textView.setText("Morning");

                    textView.setTextSize(26);
                    startText.setTextSize(26);

                    count = 0;
                }
            }

            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.dark);

                    startText.setText("Good ");
                    textView.setText("Night");

                    textView.setTextSize(26);
                    startText.setTextSize(26);

                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.light);

                    startText.setText("Good ");
                    textView.setText("Morning");

                    textView.setTextSize(26);
                    startText.setTextSize(26);

                    count = 0;
                }
            }

        });

    }

    public void openWebsite(View v) {
        Intent openSite = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jackshen.herokuapp.com/"));
        startActivity(openSite);
    }

    public void openGallery(View v) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    public void openBooks(View v) {
        Intent intent = new Intent(this, BookActivity.class);
        startActivity(intent);
    }

    public void openInfograph(View v) {
        Intent intent = new Intent(this, InfographicActivity.class);
        startActivity(intent);
    }
}
