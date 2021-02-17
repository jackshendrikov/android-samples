package ua.kpi.comsys.io8227.jackshen;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class BookAboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_book);

        Book fullBook = (Book) getIntent().getSerializableExtra("book_full");


        ImageView cover = findViewById(R.id.image_full);
        assert fullBook != null;
        if (fullBook.getImageUrl().equals("")) {
            cover.setImageResource(R.drawable.noimage);
        } else {
            new BookAdapter.DownloadImage(cover).execute(fullBook.getImageUrl());
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