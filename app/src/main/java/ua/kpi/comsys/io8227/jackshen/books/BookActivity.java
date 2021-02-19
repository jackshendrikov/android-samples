package ua.kpi.comsys.io8227.jackshen.books;

import android.app.LoaderManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Outline;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.kpi.comsys.io8227.jackshen.R;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /** URL for book data */
    public static final String REQUEST_URL = "";

    /**
     * Constant value for the book loader ID.
     * We can choose any int, it`s really needed for multiple loaders
     */
    private static final int BOOK_LOADER_ID = 1;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** Handler for the list of books */
    List<Book> books = new ArrayList<>();

    /** InputText for the book search */
    private TextInputEditText mSearchText;

    /** ImageButton for the book search */
    private ImageView mSearchButton;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;

    /** Loading indicator */
    private View mLoadingIndicator;

    private LoaderManager mLoaderManager;

    private String mQueryText;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity on main thread.
        // Bundle holds previous state when re-initialized
        super.onCreate(savedInstanceState);

        // Inflate the activity's UI
        setContentView(R.layout.activity_book);

        // Setup UI to hide soft keyboard when clicked outside the {@link EditText}
        setupUI(findViewById(R.id.main_parent));

        // Find a reference to the {@link bookListView} in the layout
        SwipeMenuListView bookListView = findViewById(R.id.list);

        mLoadingIndicator = findViewById(R.id.progress_bar);
        mLoadingIndicator.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            mQueryText = savedInstanceState.getString(REQUEST_URL);
        }

        // Set empty view when there is no data
        mEmptyTextView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, books);

        // Set the adapter on the {@link bookListView} so the list can be populated in UI
        bookListView.setAdapter(mAdapter);

        // Create SwipeMenu so we can delete items of {@link books}
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        bookListView.setMenuCreator(creator);

        // Get a reference to the {@link mSearchButton} to implement button click via keyboard
        mSearchButton = findViewById(R.id.buttonSearch);

        // Get a reference to the user input edit text view
        mSearchText = findViewById(R.id.inputSearch);

        // Set the an {@link setOnClickListener} on the ImageView
        // Implement search when user click on the button
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSearch(Objects.requireNonNull(mSearchText.getText()).toString());
            }
        });

        // Set the an {@link OnEditorActionListener} on the editable text view
        // Implement search button click when user presses the done button on the keyboard
        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Check whether the done button is pressed on the keyboard
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // User has finished entering text
                    // Perform the search button click programmatically
                    onClickSearch(Objects.requireNonNull(mSearchText.getText()).toString());

                    // Return true on successfully handling the action
                    return true;
                }

                // Do not perform any task when user is actually entering text
                // in the editable text view
                return false;
            }
        });

        // Set the an {@link setOnMenuItemClickListener} on the {@link bookListView}
        // Delete {@link books} item when user click on MenuItem
        bookListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                mAdapter.remove(mAdapter.getItem(position));
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        // Set the an {@link setOnClickListener} on the {@link bookListView}
        // Open full information about specific book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Book currentBook = mAdapter.getItem(position);

                Intent intent = new Intent(BookActivity.this, BookAboutActivity.class);
                intent.putExtra("book_full", currentBook);

                startActivity(intent);

            }
        });


        // Initialize the loader
        if (mQueryText != null) {
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        }

        // Check if the book list has been updated, if so, add new values ​​to the activity
        updateListView();

        Button mAddButton = findViewById(R.id.buttonAdd);

        //Stylization button for adding books
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);
                outline.setRoundRect(0, 0, shapeSize, shapeSize, shapeSize / 2);
            }
        };
        mAddButton.setOutlineProvider(viewOutlineProvider);
        mAddButton.setClipToOutline(true);

    }


    /** This method is called when the user hits the search button */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickSearch(String searchText) {
        mSearchText.clearFocus();
        mAdapter.clear();
        mEmptyTextView.setVisibility(View.GONE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = Objects.requireNonNull(connMgr).getActiveNetworkInfo();

        try {
            // If there is a network connection -> get data
            if (networkInfo != null && networkInfo.isConnected()) {
                if (!searchText.isEmpty() && searchText.length() >= 3 && searchText != null) {
                    String bookName = URLEncoder.encode(searchText.trim().replaceAll(" ", "%20"), "UTF-8");

                    // Set the URL with the suitable bookName
                    mQueryText = "https://api.itbook.store/1.0/search/" + bookName;

                    // Show the loading indicator.
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    // Create a bundle called queryBundle
                    Bundle queryBundle = new Bundle();

                    // Use putString with REQUEST_URL as the key and the String value of the URL as the value
                    queryBundle.putString(REQUEST_URL, mQueryText);

                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    mLoaderManager = getLoaderManager();

                    // Get the loader initialized. Go through the above specified int ID constant
                    // and pass the bundle to null.
                    Loader<String> BooksSearchLoader = mLoaderManager.getLoader(BOOK_LOADER_ID);
                    if (BooksSearchLoader == null) {
                        mLoaderManager.initLoader(BOOK_LOADER_ID, queryBundle, BookActivity.this);
                    } else {
                        mLoaderManager.restartLoader(BOOK_LOADER_ID, queryBundle, BookActivity.this);
                    }
                } else {
                    Toast.makeText(this, "You need to introduce some text to search (>=3 symbols)", Toast.LENGTH_LONG).show();
                }
            } else {
                // Otherwise, display error
                // First, hide loading indicator so error message will be visible
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                // Update empty state with no connection error message
                Toast.makeText(BookActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                mEmptyTextView.setText("No Internet Connection");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /** This method is called when the user hits the add button */
    public void onClickAdd(View view) {
        Intent intent = new Intent(this, BookAddActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the user create new Book item and we need to add it to our
     * books List
     */
    public void updateListView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            books.add(new Book(getIntent().getStringExtra("title_new"),
                    getIntent().getStringExtra("subtitle_new"),
                    "Unknown",
                    "Unknown",
                    getIntent().getStringExtra("isbn_new"),
                    "undefined",
                    "undefined",
                    getIntent().getStringExtra("rate_new"),
                    "",
                    "$" + getIntent().getStringExtra("price_new"),
                    ""
            ));
            mAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Set up touch listeners on all parts of the UI besides the {@link EditText} so that the user
     * can click out to hide the soft keypad
     */
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Hide keypad
                    v.performClick();
                    hideSoftKeyboard(BookActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion
        if (view instanceof ViewGroup) {
            // Current view is a {@Link ViewGroup}
            // Traverse the {@link ViewGroup}, over each child
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                // Get the current child view
                View innerView = ((ViewGroup) view).getChildAt(i);
                // Set up touch listeners on non-text box views
                setupUI(innerView);
            }
        }
    }

    /**
     * This method hides the soft keypad that pops up when there are views that solicit user input
     */
    public static void hideSoftKeyboard(Activity activity) {
        // Get the activity's input method service
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            if (activity.getCurrentFocus() == null)
                return;
            if (activity.getCurrentFocus().getWindowToken() == null)
                return;
            // Hide the soft keypad
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle args) {
        return new BookLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Set empty text to display "No books found."
        mEmptyTextView.setText("No books found");
        mEmptyTextView.setVisibility(View.VISIBLE);

        // Hide the indicator after the data is appeared
        mLoadingIndicator.setVisibility(View.GONE);

        // Clear the adapter pf previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Clear existing data on adapter as loader is reset
        mAdapter.clear();
    }

    /** Save the data about url */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(REQUEST_URL, mQueryText);
    }

}
