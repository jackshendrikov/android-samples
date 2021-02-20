package ua.kpi.comsys.io8227.jackshen.books;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.kpi.comsys.io8227.jackshen.books.BookContract.BookEntry;


/**
 * Database helper for Books part of application. Manages database creation and version management
 */
public class BookDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME  = "bookdb.db";

    /** Database version. Increment when database version is changed */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the book table
        String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry.BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.BOOK_TITLE + " TEXT NOT NULL, "
                + BookEntry.BOOK_SUBTITLE + " TEXT, "
                + BookEntry.BOOK_AUTHORS + " TEXT, "
                + BookEntry.BOOK_PUBLISHER + " TEXT, "
                + BookEntry.BOOK_ISBN + " BIGINT, "
                + BookEntry.BOOK_PAGES + " SMALLINT, "
                + BookEntry.BOOK_YEAR + " SMALLINT, "
                + BookEntry.BOOK_RATE + " FLOAT NOT NULL DEFAULT 0.0, "
                + BookEntry.BOOK_DESCRIPTION + " TEXT,"
                + BookEntry.BOOK_PRICE + " FLOAT NOT NULL DEFAULT 0.0, "
                + BookEntry.BOOK_IMAGE + " TEXT)";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME);
        onCreate(db);
    }

}