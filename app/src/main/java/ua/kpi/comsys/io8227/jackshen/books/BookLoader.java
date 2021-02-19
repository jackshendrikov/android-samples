package ua.kpi.comsys.io8227.jackshen.books;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.util.List;


/** Using AsyncTask to load a list of books by network request to a certain URL. */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /** Query URL **/
    private Bundle mArgs;

    /** Cache the old books */
    private List<Book> cachedBooks;


    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    BookLoader(Context context, Bundle url) {
        super(context);
        mArgs = url;
    }


    /** Forcing the loader to make an HTTP request and start downloading the required data */
    @Override
    protected void onStartLoading() {
        // If args is null, return.
        if (mArgs == null) {
            return;
        }

        // If books is not null, deliver that result. Otherwise, force a load
        if (cachedBooks != null) {
            deliverResult(cachedBooks);
        } else {
            forceLoad();
        }
    }

    /**
     *  This method is called in a background thread and takes care of the
     *  generating new data from the given JSON file
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public List<Book> loadInBackground() {
        //  Extract the search query from the args using our constant
        String searchUrl = mArgs.getString(BookActivity.REQUEST_URL);

        // Check for valid string url
        if (searchUrl == null || TextUtils.isEmpty(searchUrl)) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books.
        return BookJSONParser.getBookData(searchUrl);
    }

    /** Override deliverResult and store the data in returnedUrl */
    @Override
    public void deliverResult(List<Book> data) {
        cachedBooks = data;
        super.deliverResult(data);
    }
}
