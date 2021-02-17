package ua.kpi.comsys.io8227.jackshen;

import android.app.LoaderManager;
import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PictureActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Picture>> {

    /** URL for image data */
    public static final String REQUEST_URL = "";

    /**
     * Constant value for the image loader ID.
     * We can choose any int, it`s really needed for multiple loaders
     */
    private static final int IMAGE_LOADER_ID = 1;

    /** Adapter for the list of images */
    private PictureAdapter mAdapter;

    /** Handler for the list of images */
    List<Picture> images = new ArrayList<>();
    List<Drawable> mImageDrawableSet = new ArrayList<>();

    /** InputText for the image search */
    private TextInputEditText mSearchText;

    /** ImageButton for the image search */
    private ImageView mSearchButton;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;

    /** Loading indicator */
    private View mLoadingIndicator;

    private LoaderManager mLoaderManager;

    private String mQueryText;

    private SpannedGridLayoutManager mGridLayoutManager;

    private int mPageNum = 24;

    RecyclerView imageListView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity on main thread.
        // Bundle holds previous state when re-initialized
        super.onCreate(savedInstanceState);

        // Inflate the activity's UI
        setContentView(R.layout.activity_gallery);

        // Setup UI to hide soft keyboard when clicked outside the {@link EditText}
        setupUI(findViewById(R.id.main_parent));

        // Find a reference to the {@link imageListView} in the layout
        imageListView = findViewById(R.id.image_recycleview);

        mGridLayoutManager = new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        // Conditions for 2x2 items
                        if (position % 8 == 1) {
                            return new SpannedGridLayoutManager.SpanInfo(3, 3);
                        } else {
                            return new SpannedGridLayoutManager.SpanInfo(1, 1);
                        }
                    }
                },
                4, // number of columns
                1f // how big is default item
        );
        imageListView.setLayoutManager(mGridLayoutManager);

        mLoadingIndicator = findViewById(R.id.progress_bar);
        mLoadingIndicator.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            mQueryText = savedInstanceState.getString(REQUEST_URL);
        }

        // Set empty view when there is no data
        mEmptyTextView = findViewById(R.id.empty_view);
        if (images.isEmpty()) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            imageListView.setVisibility(View.GONE);
        } else {
            imageListView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
        }

        // Create a new adapter that takes an empty list of images as input
        mAdapter = new PictureAdapter(this, images, mImageDrawableSet);

        // Set the adapter on the {@link imageListView} so the list can be populated in UI
        imageListView.setAdapter(mAdapter);


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



        /*
         * Initialize the loader
         */
        if (mQueryText != null) {
            getLoaderManager().initLoader(IMAGE_LOADER_ID, null, this);
        }

    }


    /** This method is called when the user hits the search button */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickSearch(String searchText) {
        mSearchText.clearFocus();

        images.clear();
        mImageDrawableSet.clear();

        mEmptyTextView.setVisibility(View.GONE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = Objects.requireNonNull(connMgr).getActiveNetworkInfo();

        try {
            // If there is a network connection -> get data
            if (networkInfo != null && networkInfo.isConnected()) {
                if (!searchText.isEmpty() && searchText.length() >= 3 && searchText != null) {
                    String imageName = URLEncoder.encode(searchText, "UTF-8");

                    // Set the URL with the suitable imageName
                    mQueryText = "https://pixabay.com/api/" + "?key=" + "19193969-87191e5db266905fe8936d565" + "&q=" + imageName + "&page=" + mPageNum;

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
                    Loader<String> ImagesSearchLoader = mLoaderManager.getLoader(IMAGE_LOADER_ID);
                    if (ImagesSearchLoader == null) {
                        mLoaderManager.initLoader(IMAGE_LOADER_ID, queryBundle, PictureActivity.this);
                    } else {
                        mLoaderManager.restartLoader(IMAGE_LOADER_ID, queryBundle, PictureActivity.this);
                    }
                } else {
                    Toast.makeText(this, "You need to introduce some text to search (>=3 symbols)", Toast.LENGTH_LONG).show();
                }
            } else {
                // Otherwise, display error
                // First, hide loading indicator so error message will be visible
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                // Update empty state with no connection error message
                Toast.makeText(PictureActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                mEmptyTextView.setText("No Internet Connection");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
                    hideSoftKeyboard(PictureActivity.this);
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
    public Loader<List<Picture>> onCreateLoader(int i, Bundle args) {
        return new PictureLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Picture>> loader, List<Picture> data) {
        // Hide the indicator after the data is appeared
        mLoadingIndicator.setVisibility(View.GONE);

        // Clear the adapter pf previous image data
        images.clear();
        mImageDrawableSet.clear();

        // If there is a valid list of {@link Picture}s, then add them to the adapter's
        // data set. This will trigger the ListView to update
        if (data != null && !data.isEmpty()) {
            images.addAll(data);

            HttpRequestImgHelper requestImgHelper = new HttpRequestImgHelper(PictureJSONParser.mPreviewImageUrls, this);
            requestImgHelper.setOnTaskExecFinishedEvent(new HttpRequestImgHelper.OnTaskExecFinished() {
                @Override
                public void OnTaskExecFinishedEvent(List<Drawable> result) {

                    mImageDrawableSet = result;

                    mAdapter = new PictureAdapter(PictureActivity.this, images, mImageDrawableSet);
                    imageListView.setAdapter(mAdapter);

                    mEmptyTextView.setVisibility(View.GONE);
                    imageListView.setVisibility(View.VISIBLE);

                    mGridLayoutManager = new SpannedGridLayoutManager(
                            new SpannedGridLayoutManager.GridSpanLookup() {
                                @Override
                                public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                                    // Conditions for 3x3 items
                                    if (position % 8 == 1) {
                                        return new SpannedGridLayoutManager.SpanInfo(3, 3);
                                    } else {
                                        return new SpannedGridLayoutManager.SpanInfo(1, 1);
                                    }
                                }
                            },
                            4, // number of columns
                            1f // how big is default item
                    );
                    imageListView.setLayoutManager(mGridLayoutManager);
                    mAdapter.notifyDataSetChanged();
                }
            });
            requestImgHelper.execute();
        }

        // Set empty text to display "No images found."
        mEmptyTextView.setText("No images found");
        mEmptyTextView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<List<Picture>> loader) {
        // Clear existing data on adapter as loader is reset
        images.clear();
        mImageDrawableSet.clear();
    }

    /** Save the data about url */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(REQUEST_URL, mQueryText);
    }
}