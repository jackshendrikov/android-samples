package ua.kpi.comsys.io8227.jackshen.books;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BookProvider extends ContentProvider {
    // Tag for the log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    // Uri matcher code for all books in the table
    private static final int BOOKS = 100;

    // Uri matcher code for one book from the table
    private static final int BOOK_ID = 101;

    // Uri matcher object to match a URI to the content
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer that is used when anything is called for the first time from the BookProvider class
    static {
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private static BookDbHelper bookDbHelper;

    @Override
    // Initializing the BookProvider and the helper object
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    // Query the URI using projection, selection, selectionArgs, etc.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        // Declare cursor that will hold the result of the query
        Cursor cursor;
        // Check if cursor matcher can match URI to the code
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry.BOOK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Return MIME type for content URI
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }

    // Insert data into provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        if (match == BOOKS) {
            return insertBook(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        String bookName = contentValues.getAsString(BookContract.BookEntry.BOOK_TITLE);
        if (TextUtils.isEmpty(bookName)) {
            throw new IllegalArgumentException("Book name required");
        }

        float bookRate = contentValues.getAsFloat(BookContract.BookEntry.BOOK_RATE);
        if (bookRate < 0 || bookRate > 5) {
            throw new IllegalArgumentException("Valid rate of book required");
        }

        float bookPrice = contentValues.getAsFloat(BookContract.BookEntry.BOOK_PRICE);
        if (bookPrice < 0) {
            throw new IllegalArgumentException("Valid book price required");
        }

        // Get a writable database and insert new book with given values
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert the row for " + uri);
            return null;
        }

        // Notify listeners about the data changes
        getContext().getContentResolver().notifyChange(uri, null);

        // Return new Uri with ID
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry.BOOK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    // Update data for selection and selectionArgs with new content values
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry.BOOK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(BookContract.BookEntry.BOOK_TITLE)) {
            String bookName = contentValues.getAsString(BookContract.BookEntry.BOOK_TITLE);
            if (TextUtils.isEmpty(bookName)) {
                throw new IllegalArgumentException("Book name required");
            }
        }

        if (contentValues.containsKey(BookContract.BookEntry.BOOK_RATE)) {
            float bookRate = contentValues.getAsFloat(BookContract.BookEntry.BOOK_RATE);
            if (bookRate < 0 || bookRate > 5) {
                throw new IllegalArgumentException("Valid rate of book required");
            }
        }

        if (contentValues.containsKey(BookContract.BookEntry.BOOK_PRICE)) {
            float bookPrice = contentValues.getAsFloat(BookContract.BookEntry.BOOK_PRICE);
            if (bookPrice < 0) {
                throw new IllegalArgumentException("Valid book price required");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // Notify listeners of the data changes
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public static boolean сheckIfExist(String isbn) {
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        // Check if ISBN exists
        String Query = "SELECT * FROM books WHERE book_isbn = ? ";
        Cursor cursor =  db.rawQuery(Query, new String[]{ isbn });

        // If ISBN doesn't exist -> add Book
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            // else if exist -> return
            cursor.close();
            return true;
        }
    }

    public static String getDBImageURL(String isbn) {
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        String Query = "SELECT book_image FROM books WHERE book_isbn = ? ";
        Cursor cursor =  db.rawQuery(Query, new String[]{ isbn });

        String imageURL = "";

        if (cursor.moveToFirst())
            imageURL = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.BOOK_IMAGE));

        cursor.close();

        return imageURL;
    }

    public static List<Book> getData(String searchText) {
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        // Check for all similarities
        String Query = "SELECT * FROM books WHERE book_title LIKE ?";
        Cursor cursor =  db.rawQuery(Query, new String[]{ "%" + searchText + "%" });

        List<Book> bookList = new ArrayList<>();

        try {
            // loop through all rows and add to list
            if (cursor.moveToFirst()) {
                do {
                    // get column indexes
                    int titleIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_TITLE);
                    int subtitleIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_SUBTITLE);
                    int authorsIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_AUTHORS);
                    int publisherIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PUBLISHER);
                    int isbnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_ISBN);
                    int pagesIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PAGES);
                    int yearIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_YEAR);
                    int rateIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_RATE);
                    int descriptionIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_DESCRIPTION);
                    int priceIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PRICE);
                    int imageIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_IMAGE);

                    // get data
                    String title = cursor.getString(titleIndex);
                    String subtitle = cursor.getString(subtitleIndex);
                    String authors = cursor.getString(authorsIndex);
                    String publisher = cursor.getString(publisherIndex);
                    String isbn = cursor.getString(isbnIndex);
                    String pages = cursor.getString(pagesIndex);
                    String year = cursor.getString(yearIndex);
                    String rate = cursor.getString(rateIndex);
                    String description = cursor.getString(descriptionIndex);
                    String price = "$" + cursor.getString(priceIndex);
                    String image = cursor.getString(imageIndex);


                    // Create a new Book object
                    Book bookObject = new Book(title, subtitle, authors, publisher, isbn, pages,
                            year, rate, description, price, image);

                    // Add book to list
                    bookList.add(bookObject);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.d("SQL Error", e.getMessage());
        } finally {
            // release all resources
            cursor.close();
            db.close();
        }

        return bookList;
    }
}
