package ua.kpi.comsys.io8227.jackshen.gallery;
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

/** Helper methods to request and retrieve image data from a specified JSON */
final class PictureJSONParser {

    /** Tag for the log messages */
    private static final String LOG_MSG = PictureJSONParser.class.getSimpleName();

    /**
     * We are creating a private constructor because no one else should create
     * the {@link PictureJSONParser} object.
     */
    private PictureJSONParser() {
    }

    /**
     * Query given JSON and return a list of {@link String} objects.
     *
     * @param requestUrl - our URL as a {@link String} object
     * @return the list of {@link Picture}s
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static List<Picture> getPictureData(String requestUrl) {

        // Create URL object
        URL url = makeUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException err) {
            Log.e(LOG_MSG, "Problem making the HTTP request.", err);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Picture}s
        // Then return the list of {@link Picture}s
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
            Log.e(LOG_MSG, "Problem retrieving the image JSON results.", err);
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
    private static List<Picture> extractDataFromJson(String imageJSON) {
        // Exit if no data was returned from the HTTP request
        if (TextUtils.isEmpty(imageJSON))
            return null;

        // Initialize list of strings to hold the extracted images
        List<Picture> images = new ArrayList<>();

        try {
            // Create JSON object from response
            JSONObject rawJSONResponse = new JSONObject(imageJSON);

            // Extract the array that holds the images
            JSONArray imageArray = rawJSONResponse.getJSONArray("hits");

            for (int i = 0; i < imageArray.length(); i++) {
                // Get the current image
                JSONObject currentImage = imageArray.getJSONObject(i);

                // Get ID of the image
                String imageID = currentImage.getString("id");

                // Get the URL of the image
                String imageUrl = currentImage.getString("webformatURL");

                // Get tags of the image
                String imageTags = currentImage.getString("tags");

                // Get width of the image
                String imageWidth = currentImage.getString("imageWidth");

                // Get height of the image
                String imageHeight = currentImage.getString("imageHeight");

                // Get number of image views
                String views = currentImage.getString("views");

                // Get number of image downloads
                String downloads = currentImage.getString("downloads");

                // Get number of image saves
                String favorites = currentImage.getString("favorites");

                // Get number of image likes
                String likes = currentImage.getString("likes");

                // Get user name
                String user = currentImage.getString("user");

                // Get user image
                String userImageURL = currentImage.getString("userImageURL");

                // Create a new {@link Picture} object with the title, subtitle, isbn13, price
                // and imageUrl from the JSON response.
                Picture image = new Picture(imageID, imageUrl, imageTags, imageWidth, imageHeight, views, downloads,
                        favorites, likes, user, userImageURL);

                // Add the new {@link Picture} to the list of images.
                images.add(image);
            }

        } catch (JSONException e) {
            // Catch the exception from the 'try' block and print a log message
            Log.e("ImageJSONParser", "Problem parsing the image JSON results", e);
        }

        // Return the list of images
        return images;
    }

}