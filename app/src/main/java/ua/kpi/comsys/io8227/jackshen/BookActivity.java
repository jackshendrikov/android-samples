package ua.kpi.comsys.io8227.jackshen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** Handler for the list of books */
    List<Book> books;

    /** InputText for the book search */
    private TextInputEditText mSearchText;

    /** ImageButton for the book search */
    private ImageView mSearchButton;

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

        // Load Data from JSON files
        books = BookJSONParser.extractDataFromJson(this);

        // Find a reference to the {@link bookListView} in the layout
        SwipeMenuListView bookListView = findViewById(R.id.list);

        // Set empty view when there is no data
        View mEmptyTextView = findViewById(R.id.empty_view);
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
                onClickSearch();
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
                    onClickSearch();
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

                Intent intent = new Intent(BookActivity.this, AboutBookActivity.class);
                intent.putExtra("book_full", currentBook);

                startActivity(intent);

            }
        });

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
    public void onClickSearch() {
        // Get a handle for the editable text view holding the user's search text and convert it
        // to lowercase string value
        String text = mSearchText.getText().toString().toLowerCase();

        // Create empty list of Books
        List<Book> booksSearch = new ArrayList<>();

        if (text.length() == 0) {
            // User has not entered any search text
            // Notify user to enter text via toast
            Toast toast = Toast.makeText(getApplicationContext(), "Please, enter text", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            // Go through all books in the current list and search if any title contains the text
            // we are looking for
            for (int i = 0; i < mAdapter.getCount(); i++) {
                if (mAdapter.getItem(i).getTitle() != null) {
                    // Get lowercase value of current book title
                    String temp = mAdapter.getItem(i).getTitle().toLowerCase();
                    // If title contains our text -> create new Book item and add it to {@link booksSearch}
                    if (temp.contains(text)) {
                        Book findBook = new Book(mAdapter.getItem(i).getTitle(),
                                mAdapter.getItem(i).getSubtitle(), mAdapter.getItem(i).getAuthor(),
                                mAdapter.getItem(i).getPublisher(), mAdapter.getItem(i).getISBN(),
                                mAdapter.getItem(i).getPages(), mAdapter.getItem(i).getYear(),
                                mAdapter.getItem(i).getRate(), mAdapter.getItem(i).getDescription(),
                                mAdapter.getItem(i).getPrice(), mAdapter.getItem(i).getImageUrl());
                        booksSearch.add(findBook);
                    }
                }
            }
            if (mAdapter.isEmpty()) {
                // No results available
                Toast toast = Toast.makeText(getApplicationContext(), "There are no results", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                SwipeMenuListView bookListView = findViewById(R.id.list);

                // Sort all books by title
                Collections.sort(booksSearch, new Comparator<Book>() {
                    @Override public int compare(Book u1, Book u2) {
                        return u1.getTitle().compareTo(u2.getTitle());
                    }
                });

                // Create a new adapter that takes an empty list of books as input
                mAdapter = new BookAdapter(this, booksSearch);

                // Set the adapter on the {@link bookListView} so the list can be populated in UI
                bookListView.setAdapter(mAdapter);

                // Notify about changes
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    /** This method is called when the user hits the add button */
    public void onClickAdd(View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
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
     * can click out to hide the soft keypad and choose the necessary filter radio boxes befitting
     * their need
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
}
