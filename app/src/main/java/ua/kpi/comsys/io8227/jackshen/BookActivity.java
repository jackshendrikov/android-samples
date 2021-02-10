package ua.kpi.comsys.io8227.jackshen;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /** URL for book data */
    private static final String REQUEST_URL = "https://api.jsonbin.io/b/6023ffbd87173a3d2f5b37e9";

    /**
     * Constant value for the book loader ID.
     * We can choose any int, it`s really needed for multiple loaders
     */
    private static final int BOOK_LOADER_ID = 1;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity on main thread.
        // Bundle holds previous state when re-initialized
        super.onCreate(savedInstanceState);

        // Inflate the activity's UI
        setContentView(R.layout.activity_book);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = findViewById(R.id.list);

        // Set empty view when there is no data
        mEmptyTextView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView} so the list can be populated in UI
        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = Objects.requireNonNull(connMgr).getActiveNetworkInfo();

        // If there is a network connection -> get data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Get the loader initialized. Go through the above specified int ID constant
            // and pass the bundle to null.
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {

            // Otherwise, display error and hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyTextView.setText("No internet connection.");
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Uri baseUri = Uri.parse(REQUEST_URL);

        return new BookLoader(this, baseUri.toString());
    }

    /**
     * The loader requests and parses information downloader from the internet on a background
     * thread pool, keeping the UI thread unblocked
     */
    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Hide progress bar
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books to display."
        mEmptyTextView.setText("No books to display.");

        // Clear the adapter of previous data
        mAdapter.clear();

        // Add valid list of books to the adapter
        if (books != null && !books.isEmpty())
            mAdapter.addAll(books);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Clear existing data on adapter as loader is reset
        mAdapter.clear();
    }
}