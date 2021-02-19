package ua.kpi.comsys.io8227.jackshen.books;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Helper methods to request and retrieve book data from a specified JSON */
final class BookJSONParser {

    /** Tag for the log messages */
    private static final String LOG_MSG = BookJSONParser.class.getSimpleName();

    /**
     * We are creating a private constructor because no one else should create
     * the {@link BookJSONParser} object.
     */
    private BookJSONParser() {
    }

    /**
     * Query given JSON and return a list of {@link String} objects.
     *
     * @param requestUrl - our URL as a {@link String} object
     * @return the list of {@link Book}s
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static List<Book> getBookData(String requestUrl) {
        // Create URL object
        URL url = makeUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException err) {
            Log.e(LOG_MSG, "Problem making the HTTP request.", err);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        // Then return the list of {@link Book}s
        return extractDataFromJson(jsonResponse);
    }

    /** Returns new URL object from the given string URL. */
    private static URL makeUrl(String strUrl) {
        // Initialize an empty {@link URL} object to hold the parsed URL from the strUrl
        URL url = null;

        // Parse valid URL from param strUrl and handle Malformed urls
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException err) {
            Log.e(LOG_MSG, "Problem building the URL!", err);
        }

        // Return valid URL
        return url;
    }

    /** Make an HTTP request to the given URL and return a String as the response. */
    private static String makeHttpRequest(URL url) throws IOException {
        // Initialize variable to hold the parsed JSON response
        String jsonResponse = "";

        // Return response if URL is null
        if (url == null) {
            return jsonResponse;
        }

        // Initialize HTTP connection object
        HttpURLConnection connection = null;

        // Initialize {@link InputStream} to hold response from request
        InputStream inputStream = null;
        try {
            // Establish connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set request type
            connection.setRequestMethod("GET");

            // Setting how long to wait on the request (in milliseconds)
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);

            // Establish connection to the URL
            connection.connect();

            // Check for successful connection
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_MSG, "Error response code: " + connection.getResponseCode());
            }
        } catch (IOException err) {
            Log.e(LOG_MSG, "Problem retrieving the book JSON results.", err);
        } finally {
            if (connection != null) {
                // Disconnect the connection after successfully making the HTTP request
                connection.disconnect();
            }

            if (inputStream != null) {
                // Close the stream after successfully parsing the request
                // This also can throw an IOException
                inputStream.close();
            }
        }

        // Return JSON as a {@link String}
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            // Decode the bits
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            // Buffer the decoded characters
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Store a line of characters from the BufferedReader
            String line = reader.readLine();

            // If not end of buffered input stream, read next line and add to output
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        // Convert chars sequence from the builder into a string and return
        return output.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static List<Book> extractDataFromJson(String bookJSON) {
        // Exit if no data was returned from the HTTP request
        if (TextUtils.isEmpty(bookJSON))
            return null;

        // Initialize list of strings to hold the extracted books
        List<Book> books = new ArrayList<>();

        String REQUEST_URL_FULL = "https://api.itbook.store/1.0/books/";

        try {
            // Create JSON object from response
            JSONObject rawJSONResponse = new JSONObject(bookJSON);

            // Extract the array that holds the books
            JSONArray bookArray = rawJSONResponse.getJSONArray("books");

            for (int i = 0; i < bookArray.length(); i++) {
                // Get the current book
                JSONObject currentBook = bookArray.getJSONObject(i);

                // Get the book's ISBN number
                String isbn13 = currentBook.getString("isbn13");

                if (!isbn13.equals("")) {
                    URL url = makeUrl(REQUEST_URL_FULL + isbn13);

                    // Perform HTTP request to the URL and receive a JSON response back
                    String jsonResponse = null;
                    try {
                        jsonResponse = makeHttpRequest(url);
                    } catch (IOException err) {
                        Log.e(LOG_MSG, "Problem making the HTTP request.", err);
                    }

                    // Get full information the current book
                    JSONObject currentFullBook = new JSONObject(Objects.requireNonNull(jsonResponse));

                    // Get the book's title
                    String title = currentFullBook.getString("title");

                    // Get the book's subtitle
                    String subtitle = currentFullBook.getString("subtitle");

                    // Extract authors only if they exist
                    String authors = "";
                    if (currentFullBook.has("authors")) {
                        // Extract array "authors"
                        String[] authorsArray = currentFullBook.getString("authors").split(",");

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
                    String publisher = currentFullBook.getString("publisher");

                    // Get the book's pages
                    String pages = currentFullBook.getString("pages");

                    // Get the book's year of publish
                    String year = currentFullBook.getString("year");

                    // Get the book's rating
                    String rating = currentFullBook.getString("rating");

                    // Get the book's description
                    String description = currentFullBook.getString("desc");

                    // Get the book's price
                    String price = currentFullBook.getString("price");

                    // Get the URL of book's cover
                    String imageUrl = currentFullBook.getString("image");

                    // Create a new {@link Book} object with the title, subtitle, isbn13, price
                    // and imageUrl from the JSON response.
                    Book book = new Book(title, subtitle, authors, publisher, isbn13, pages, year,
                            rating, description, price, imageUrl);

                    // Add the new {@link Book} to the list of books.
                    books.add(book);


                } else {
                    // Get the book's title
                    String title = currentBook.getString("title");

                    // Get the book's subtitle
                    String subtitle = currentBook.getString("subtitle");

                    // Get the book's price
                    String price = currentBook.getString("price");

                    // Get the URL of book's cover
                    String imageUrl = currentBook.getString("image");


                    // Create a new {@link Book} object with the title, subtitle, isbn13, price
                    // and imageUrl from the JSON response.
                    Book book = new Book(title, subtitle, "", "", isbn13, "", "",
                            "0.0", "", price, imageUrl);

                    // Add the new {@link Book} to the list of books.
                    books.add(book);
                }
            }

        } catch (JSONException e) {
            // Catch the exception from the 'try' block and print a log message
            Log.e("BookJSONParser", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }

}