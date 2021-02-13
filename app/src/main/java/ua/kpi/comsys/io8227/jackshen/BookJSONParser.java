package ua.kpi.comsys.io8227.jackshen;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/** Helper methods to request and retrieve book data from a specified JSON */
final class BookJSONParser {

    private static int[] booksData = {
            R.raw._9780321856715,
            R.raw._9780321862969,
            R.raw._9781118841471,
            R.raw._9781430236054,
            R.raw._9781430237105,
            R.raw._9781430238072,
            R.raw._9781430245124,
            R.raw._9781430260226,
            R.raw._9781449308360,
            R.raw._9781449342753
    };

    /** Tag for the log messages */
    private static final String LOG_MSG = BookJSONParser.class.getSimpleName();

    /**
     * We are creating a private constructor because no one else should create
     * the {@link BookJSONParser} object.
     */
    private BookJSONParser() { }


    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response.
     */
    private static String readFromStream(Context context, int resID) throws IOException {
        StringBuilder output = new StringBuilder();

        // Decode the bits
        InputStreamReader inputStreamReader = new InputStreamReader(context.getResources().openRawResource(resID), Charset.forName("UTF-8"));

        // Buffer the decoded characters
        BufferedReader reader = new BufferedReader(inputStreamReader);

        // Store a line of characters from the BufferedReader
        String line = reader.readLine();

        // If not end of buffered input stream, read next line and add to output
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }

        // Convert chars sequence from the builder into a string and return
        return output.toString();
    }


    public static List<Book> extractDataFromJson(Context context) {
        // Initialize list of strings to hold the extracted books
        List<Book> books = new ArrayList<>();

        try {
            for (int value : booksData) {
                String rawJSONResponse = readFromStream(context, value);

                // Get the current book
                JSONObject currentBook = new JSONObject(rawJSONResponse);

                // Get the book's title
                String title = currentBook.getString("title");

                // Get the book's subtitle
                String subtitle = currentBook.getString("subtitle");

                // Extract authors only if they exist
                String authors = "";
                if (currentBook.has("authors")) {
                    // Extract array "authors"
                    String[] authorsArray = currentBook.getString("authors").split(",");

                    for (int j = 0; j < authorsArray.length; j++) {
                        if (j == 0)
                            authors += authorsArray[j];
                        else if (j <= 2)
                            authors += " | " + authorsArray[j];
                        else
                            authors += " and others";
                    }
                } else {
                    authors = "Unknown authors";
                }

                // Get the book's publisher
                String publisher = currentBook.getString("publisher");

                // Get the book's ISBN number
                String isbn13 = currentBook.getString("isbn13");

                // Get the book's pages
                String pages = currentBook.getString("pages");

                // Get the book's year of publish
                String year = currentBook.getString("year");

                // Get the book's rating
                String rating = currentBook.getString("rating");

                // Get the book's description
                String description = currentBook.getString("desc");

                // Get the book's price
                String price = currentBook.getString("price");

                // Get the URL of book's cover
                String imageUrl = currentBook.getString("image");

                // Create a new {@link Book} object with the title, subtitle, isbn13, price
                // and imageUrl from the JSON response.
                Book book = new Book(title, subtitle, authors, publisher, isbn13, pages, year,
                        rating, description, price, imageUrl);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // Catch the exception from the 'try' block and print a log message
            Log.e("BookJSONParser", "Problem parsing the book JSON results", e);
        } catch (IOException e) {
            Log.e(LOG_MSG, "Problem retrieving the book JSON results.", e);
        }

        // Return the list of books
        return books;
    }

}