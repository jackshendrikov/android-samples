package ua.kpi.comsys.io8227.jackshen.gallery;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PictureProvider extends ContentProvider {
    // Tag for the log messages
    public static final String LOG_TAG = PictureProvider.class.getSimpleName();

    // Uri matcher code for all images in the table
    private static final int IMAGES = 100;

    // Uri matcher code for one image from the table
    private static final int IMAGE_ID = 101;

    // Uri matcher object to match a URI to the content
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer that is used when anything is called for the first time from the PictureProvider class
    static {
        uriMatcher.addURI(PictureContract.CONTENT_AUTHORITY, PictureContract.PATH_IMAGES, IMAGES);
        uriMatcher.addURI(PictureContract.CONTENT_AUTHORITY, PictureContract.PATH_IMAGES + "/#", IMAGE_ID);
    }

    private static PictureDbHelper imagesDbHelper;

    @Override
    // Initializing the PictureProvider and the helper object
    public boolean onCreate() {
        imagesDbHelper = new PictureDbHelper(getContext());
        return true;
    }

    // Query the URI using projection, selection, selectionArgs, etc.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = imagesDbHelper.getReadableDatabase();
        // Declare cursor that will hold the result of the query
        Cursor cursor;
        // Check if cursor matcher can match URI to the code
        int match = uriMatcher.match(uri);
        switch (match) {
            case IMAGES:
                cursor = database.query(PictureContract.PictureEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case IMAGE_ID:
                selection = PictureContract.PictureEntry.IMAGE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PictureContract.PictureEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
            case IMAGES:
                return PictureContract.PictureEntry.CONTENT_LIST_TYPE;
            case IMAGE_ID:
                return PictureContract.PictureEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }

    // Insert data into provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        if (match == IMAGES) {
            return insertImage(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    private Uri insertImage(Uri uri, ContentValues contentValues) {
        // Get a writable database and insert new image with given values
        SQLiteDatabase database = imagesDbHelper.getWritableDatabase();
        long id = database.insert(PictureContract.PictureEntry.TABLE_NAME, null, contentValues);
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
        SQLiteDatabase database = imagesDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case IMAGES:
                rowsDeleted = database.delete(PictureContract.PictureEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGE_ID:
                selection = PictureContract.PictureEntry.IMAGE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PictureContract.PictureEntry.TABLE_NAME, selection, selectionArgs);
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
            case IMAGES:
                return updateImage(uri, contentValues, selection, selectionArgs);
            case IMAGE_ID:
                selection = PictureContract.PictureEntry.IMAGE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateImage(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateImage(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = imagesDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(PictureContract.PictureEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // Notify listeners of the data changes
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public static boolean сheckIfExist(String netID) {
        SQLiteDatabase db = imagesDbHelper.getReadableDatabase();

        // Check if NET_ID exists
        String Query = "SELECT * FROM images WHERE image_net_id = ? ";
        Cursor cursor =  db.rawQuery(Query, new String[]{ netID });

        // If NET_ID doesn't exist -> add Picture
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            // else if exist -> return
            cursor.close();
            return true;
        }
    }


    public static List<Picture> getData(String searchText) {
        SQLiteDatabase db = imagesDbHelper.getReadableDatabase();

        // Check for all similarities
        String Query = "SELECT * FROM images WHERE image_tags LIKE ?";
        Cursor cursor =  db.rawQuery(Query, new String[]{ "%" + searchText + "%" });

        List<Picture> imageList = new ArrayList<>();

        try {
            // loop through all rows and add to list
            if (cursor.moveToFirst()) {
                do {
                    // get column indexes
                    int netIDIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_NET_ID);
                    int urlIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_URL);
                    int tagsIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_TAGS);
                    int widthIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_WIDTH);
                    int heightIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_HEIGHT);
                    int viewsIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_VIEWS);
                    int downloadsIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_DOWNLOADS);
                    int favoritesIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_FAVORITES);
                    int likesIndex = cursor.getColumnIndex(PictureContract.PictureEntry.IMAGE_LIKES);
                    int userNameIndex = cursor.getColumnIndex(PictureContract.PictureEntry.USER_NAME);
                    int userImageIndex = cursor.getColumnIndex(PictureContract.PictureEntry.USER_IMAGE);

                    // get data
                    String id = cursor.getString(netIDIndex);
                    String url = cursor.getString(urlIndex);
                    String tags = cursor.getString(tagsIndex);
                    String width = cursor.getString(widthIndex);
                    String height = cursor.getString(heightIndex);
                    String views = cursor.getString(viewsIndex);
                    String downloads = cursor.getString(downloadsIndex);
                    String favorites = cursor.getString(favoritesIndex);
                    String likes = cursor.getString(likesIndex);
                    String userName = cursor.getString(userNameIndex);
                    String userImage = cursor.getString(userImageIndex);


                    // Create a new Picture object
                    Picture imageObject = new Picture(id, url, tags, width, height, views, downloads,
                            favorites, likes, userName, userImage);

                    // Add image to list
                    imageList.add(imageObject);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.d("SQL Error", e.getMessage());
        } finally {
            // release all resources
            cursor.close();
            db.close();
        }

        return imageList;
    }
}
