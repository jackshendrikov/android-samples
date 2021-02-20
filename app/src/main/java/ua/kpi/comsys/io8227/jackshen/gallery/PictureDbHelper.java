package ua.kpi.comsys.io8227.jackshen.gallery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.kpi.comsys.io8227.jackshen.gallery.PictureContract.PictureEntry;

/**
 * Database helper for Pictures part of application. Manages database creation and version management
 */
public class PictureDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME  = "imagesdb.db";

    /** Database version. Increment when database version is changed */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link PictureDbHelper}.
     *
     * @param context of the app
     */
    public PictureDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the picture table
        String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + PictureEntry.TABLE_NAME + " ("
                + PictureEntry.IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PictureEntry.IMAGE_NET_ID + " INTEGER NOT NULL, "
                + PictureEntry.IMAGE_TAGS + " TEXT NOT NULL, "
                + PictureEntry.IMAGE_URL + " TEXT, "
                + PictureEntry.IMAGE_WIDTH + " SMALLINT, "
                + PictureEntry.IMAGE_HEIGHT + " SMALLINT, "
                + PictureEntry.IMAGE_VIEWS + " INTEGER, "
                + PictureEntry.IMAGE_DOWNLOADS + " INTEGER, "
                + PictureEntry.IMAGE_FAVORITES + " INTEGER, "
                + PictureEntry.IMAGE_LIKES + " INTEGER, "
                + PictureEntry.USER_NAME + " TEXT,"
                + PictureEntry.USER_IMAGE + " TEXT)";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PictureEntry.TABLE_NAME);
        onCreate(db);
    }

}
