package ua.kpi.comsys.io8227.jackshen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.InputStream;
import java.util.List;
import java.util.Objects;


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

    /** Class to download an image from URL */
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
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