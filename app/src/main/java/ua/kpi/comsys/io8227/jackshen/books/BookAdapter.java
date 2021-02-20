package ua.kpi.comsys.io8227.jackshen.books;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        final Context context = BookActivity.getAppContext();

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Book data
        String title = Objects.requireNonNull(currentBook).getTitle();
        String subtitle = currentBook.getSubtitle();
        String isbn13 = currentBook.getISBN();
        String authors = currentBook.getAuthor();
        String publisher = currentBook.getPublisher();
        String pages = currentBook.getPages();
        String year = currentBook.getYear();
        String rate = Objects.requireNonNull(currentBook).getRate();
        String description = currentBook.getDescription();
        String price = currentBook.getPrice();
        String imageURL = currentBook.getImageUrl();

        // Find the TextView with view ID rating
        TextView ratingView = listItemView.findViewById(R.id.ratingBook);
        // Format the rating to show 1 decimal place
        String formattedRating = formatRating(Double.parseDouble(rate));
        // Display the rating of the current book in that TextView
        ratingView.setText(formattedRating);

        // Set the proper background color on the rating circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable ratingCircle = (GradientDrawable) ratingView.getBackground();
        // Get the appropriate background color based on the current book rating
        int ratingColor = getRatingColor(Double.parseDouble(rate));
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
        final ImageView imageView = listItemView.findViewById(R.id.imageBook);


        // Display the title of the current book in that TextView
        titleView.setText(title);

        // Display the subtitle of the current book in that TextView
        subtitleView.setText(subtitle);

        // Display the ISBN of the current book in that TextView
        isbnView.setText(isbn13);

        // Display the price of the current book in that TextView
        priceView.setText(price);

        // Display image in the ImageView widget
        if (imageURL == null || imageURL.equals("")) {
            imageView.setImageResource(R.drawable.noimage);
        } else {
            // If network is available -> load images from Internet
            if (BookActivity.isNetworkConnected(context)) {
                String imageTitle = imageURL.substring(imageURL.lastIndexOf("/") + 1);
                Picasso.get()
                        .load(imageURL)
                        .into(picassoImageTarget(
                                context,
                                "images",
                                imageTitle,
                                isbn13
                        ));

                Picasso.get().load(imageURL).into(imageView);
            } else {
                // Load image from local storage if network unavailable
                Picasso.get()
                        .load(new File(imageURL))
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(context, "Cannot load image from storage", Toast.LENGTH_SHORT).show();
                                imageView.setImageResource(R.drawable.noimage);
                            }
                        });
            }

        }

        if (BookActivity.isNetworkConnected(context)) {
            // Check if current Book object exist in our DB, if not -> add
            if (!BookProvider.сheckIfExist(isbn13)) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(BookContract.BookEntry.BOOK_TITLE, title);
                contentValues.put(BookContract.BookEntry.BOOK_SUBTITLE, subtitle);
                contentValues.put(BookContract.BookEntry.BOOK_ISBN, isbn13);
                contentValues.put(BookContract.BookEntry.BOOK_AUTHORS, authors);
                contentValues.put(BookContract.BookEntry.BOOK_PUBLISHER, publisher);
                contentValues.put(BookContract.BookEntry.BOOK_PAGES, pages);
                contentValues.put(BookContract.BookEntry.BOOK_YEAR, year);
                contentValues.put(BookContract.BookEntry.BOOK_RATE, rate);
                contentValues.put(BookContract.BookEntry.BOOK_DESCRIPTION, description);
                contentValues.put(BookContract.BookEntry.BOOK_PRICE, price.replaceAll("[^0-9.]", ""));

                context.getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, contentValues);

                Log.i("book", "new book add to DB: " + isbn13);
            }
        }

        return listItemView;
    }

    private Target picassoImageTarget(final Context context, final String imageDir,
                                      final String imageName, final String isbn) {

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
                            values.put(BookContract.BookEntry.BOOK_IMAGE, myImageFile.getAbsolutePath());

                            // add image path to our DB
                            context.getContentResolver().update(
                                    BookContract.BookEntry.CONTENT_URI,
                                    values,
                                    "book_isbn = ?",
                                    new String[]{isbn});

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

}