package ua.kpi.comsys.io8227.jackshen.books;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;

import ua.kpi.comsys.io8227.jackshen.R;


public class BookAboutActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_book);

        Book fullBook = (Book) getIntent().getSerializableExtra("book_full");


        ImageView cover = findViewById(R.id.image_full);
        assert fullBook != null;
        if (fullBook.getImageUrl() == null || fullBook.getImageUrl().equals("")) {
            cover.setImageResource(R.drawable.noimage);
        } else {
            if (BookActivity.isNetworkConnected(this))
                Picasso.get().load(fullBook.getImageUrl()).into(cover);
            else Picasso.get().load(new File(fullBook.getImageUrl())).into(cover);
        }

        TextView title = findViewById(R.id.title_full);
        title.setText(fullBook.getTitle());

        TextView subtitle = findViewById(R.id.subtitle_full);
        subtitle.setText(fullBook.getSubtitle());

        TextView authors = findViewById(R.id.authors_full);
        authors.setText(fullBook.getAuthor());

        TextView publisher = findViewById(R.id.publisher_full);
        publisher.setText(fullBook.getPublisher());

        TextView isbn = findViewById(R.id.isbn_full);
        isbn.setText(fullBook.getISBN());

        TextView pages = findViewById(R.id.pages_full);
        pages.setText(fullBook.getPages());

        TextView year = findViewById(R.id.year_full);
        year.setText(fullBook.getYear());

        RatingBar rate = findViewById(R.id.rate_full);
        rate.setRating(Float.parseFloat(fullBook.getRate()));

        TextView price = findViewById(R.id.price_full);
        price.setText(fullBook.getPrice());

        TextView description = findViewById(R.id.description_full);
        description.setText(fullBook.getDescription());

    }
}