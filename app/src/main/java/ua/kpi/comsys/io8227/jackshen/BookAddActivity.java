package ua.kpi.comsys.io8227.jackshen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static ua.kpi.comsys.io8227.jackshen.BookActivity.hideSoftKeyboard;

public class BookAddActivity extends AppCompatActivity {

    String rate = "0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Setup UI to hide soft keyboard when clicked outside the {@link EditText}
        setupUI(findViewById(R.id.add_main_parent));

        RatingBar inputRate = findViewById(R.id.inputRate);
        inputRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = String.valueOf(ratingBar.getRating());
            }
        });
    }

    public void onClickAdd(View view){
        final Intent intent = new Intent(this, BookActivity.class);

        EditText inputTitle = findViewById(R.id.inputTitle);
        String title = inputTitle.getText().toString();

        EditText inputSubtitle = findViewById(R.id.inputSubtitle);
        String subtitle = inputSubtitle.getText().toString();

        EditText inputPrice = findViewById(R.id.inputPrice);
        String price = inputPrice.getText().toString();

        EditText inputISBN = findViewById(R.id.inputISBN);
        String isbn = inputISBN.getText().toString();

        // Check for correct title value
        if (title.equals("")) {
            Toast.makeText(this, "Please enter the title of the book", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.putExtra("title_new", title);
            intent.putExtra("subtitle_new", subtitle);
        }

        // Check for correct price value
        if (price.equals("") || Float.valueOf(price) < 0) {
            Toast.makeText(this, "Please enter correct price", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.putExtra("price_new", price);
        }

        // Check for correct ISBN value
        if (isbn.equals("")) {
            intent.putExtra("isbn_new", "noid");
        } else if (isbn.length() == 13) {
            intent.putExtra("isbn_new", isbn);
        } else {
            Toast.makeText(this, "Please enter correct ISBN number", Toast.LENGTH_SHORT).show();
            return;
        }

        intent.putExtra("rate_new", this.rate);

        startActivity(intent);
        finish();
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
                    hideSoftKeyboard(BookAddActivity.this);
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


}
