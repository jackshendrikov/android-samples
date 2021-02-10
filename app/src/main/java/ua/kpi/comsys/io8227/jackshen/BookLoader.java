package ua.kpi.comsys.io8227.jackshen;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


/** Using AsyncTask to load a list of books by network request to a certain URL. */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /** Query URL **/
    final private String mUrl;


    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }


    /** Forcing the loader to make an HTTP request and start downloading the required data */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     *  This method is called in a background thread and takes care of the
     *  generating new data from the given JSON file
     */
    @Override
    public List<Book> loadInBackground() {
        // Check for valid string url
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of books.
        return BookJSONParser.getBookData(mUrl);
    }
}