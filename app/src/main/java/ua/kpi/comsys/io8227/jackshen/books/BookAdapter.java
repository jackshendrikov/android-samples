package ua.kpi.comsys.io8227.jackshen.books;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import ua.kpi.comsys.io8227.jackshen.R;


/**
 * Class {@link BookAdapter} is used to create a list item layout for each book
 * in the data source (a list of {@link Book} objects).
 *
 * An adapter view such as ListView will be provided with these
 * list item layouts to be presented to the user.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param context of the app
     * @param books - list of books
     */

    BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns the view of the list item that shows information about the book in the book list.
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing convertView that we can reuse, otherwise,
        // if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Find the TextView with view ID rating
        TextView ratingView = listItemView.findViewById(R.id.ratingBook);
        // Format the rating to show 1 decimal place
        String formattedRating = formatRating(Double.parseDouble(Objects.requireNonNull(currentBook).getRate()));
        // Display the rating of the current book in that TextView
        ratingView.setText(formattedRating);

        // Set the proper background color on the rating circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable ratingCircle = (GradientDrawable) ratingView.getBackground();
        // Get the appropriate background color based on the current book rating
        int ratingColor = getRatingColor(Double.parseDouble(currentBook.getRate()));
        // Set the color on the rating circle
        ratingCircle.setColor(ratingColor);


        // Find the TextView with view ID titleBook
        TextView titleView = listItemView.findViewById(R.id.titleBook);

        // Find the TextView with view ID subtitleBook
        TextView subtitleView = listItemView.findViewById(R.id.subtitleBook);

        // Find the TextView with view ID isbnBook
        TextView isbnView = listItemView.findViewById(R.id.isbnBook);

        // Find the TextView with view ID priceBook
        TextView priceView = listItemView.findViewById(R.id.priceBook);

        // Find the ImageView with view ID imageBook
        ImageView imageView = listItemView.findViewById(R.id.imageBook);



        // Display the title of the current book in that TextView
        titleView.setText(Objects.requireNonNull(currentBook).getTitle());

        // Display the subtitle of the current book in that TextView
        subtitleView.setText(currentBook.getSubtitle());

        // Display the ISBN of the current book in that TextView
        isbnView.setText(currentBook.getISBN());

        // Display the price of the current book in that TextView
        priceView.setText(currentBook.getPrice());

        // Display image in the ImageView widget
        if (currentBook.getImageUrl().equals("")) {
            imageView.setImageResource(R.drawable.noimage);
        } else {
            new DownloadImage(imageView).execute(currentBook.getImageUrl());
        }

        return listItemView;
    }

    /**
     * Return the color for the rating circle based on the rating of the book.
     *
     * @param rating of the book
     */
    private int getRatingColor(double rating) {
        int ratingColorResourceId;
        int ratingFloor = (int) Math.floor(rating);
        switch (ratingFloor) {
            case 0:
            case 1:
                ratingColorResourceId = R.color.rating1;
                break;
            case 2:
                ratingColorResourceId = R.color.rating2;
                break;
            case 3:
                ratingColorResourceId = R.color.rating3;
                break;
            case 4:
                ratingColorResourceId = R.color.rating4;
                break;
            default:
                ratingColorResourceId = R.color.rating5;
                break;
        }

        return ContextCompat.getColor(getContext(), ratingColorResourceId);
    }

    /**
     * Return the formatted rating string showing 1 decimal place from a decimal rating value.
     */
    private String formatRating(double rating) {
        DecimalFormat ratingFormat = new DecimalFormat("0.0");
        return ratingFormat.format(rating);
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