package ua.kpi.comsys.io8227.jackshen;

import android.text.TextUtils;
import android.util.Log;

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
    static List<Book> getBookData(String requestUrl) {
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

    private static List<Book> extractDataFromJson(String bookJSON) {
        // Exit if no data was returned from the HTTP request
        if (TextUtils.isEmpty(bookJSON))
            return null;

        // Initialize list of strings to hold the extracted books
        List<Book> books = new ArrayList<>();

        try {
            // Create JSON object from response
            JSONObject rawJSONResponse = new JSONObject(bookJSON);

            // Extract the array that holds the books
            JSONArray bookArray = rawJSONResponse.getJSONArray("books");

            for (int i = 0; i < bookArray.length(); i++) {
                // Get the current book
                JSONObject currentBook = bookArray.getJSONObject(i);

                // Get the book's title
                String title = currentBook.getString("title");

                // Get the book's subtitle
                String subtitle = currentBook.getString("subtitle");

                // Get the book's ISBN number
                String isbn13 = currentBook.getString("isbn13");

                // Get the book's price
                String price = currentBook.getString("price");

                // Get the URL of book's cover
                String imageUrl = currentBook.getString("image");

                // Create a new {@link Book} object with the title, subtitle, isbn13, price
                // and imageUrl from the JSON response.
                Book book = new Book(title, subtitle, isbn13, price, imageUrl);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // Catch the exception from the 'try' block and print a log message
            Log.e("BookJSONParser", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }

}