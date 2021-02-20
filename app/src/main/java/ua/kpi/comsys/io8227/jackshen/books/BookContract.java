package ua.kpi.comsys.io8227.jackshen.books;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Book fragment of application
 */
public final class BookContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */

    static final String CONTENT_AUTHORITY = "ua.kpi.comsys.io8227.jackshen.books";

    static final String PATH_BOOKS = "books";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {}

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single item.
     */
    public static abstract class BookEntry implements BaseColumns {

        /** Complete CONTENT_URI */
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /** The MIME type of the {@link #CONTENT_URI} for a list of books. */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** The MIME type of the {@link #CONTENT_URI} for a single book. */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // Name of database table
        public final static String TABLE_NAME = "books";

        public final static String BOOK_ID = BaseColumns._ID;

        public final static String BOOK_TITLE = "book_title";

        public final static String BOOK_SUBTITLE = "book_subtitle";

        public final static String BOOK_AUTHORS = "book_authors";

        public final static String BOOK_PUBLISHER = "book_publisher";

        public final static String BOOK_ISBN = "book_isbn";

        public final static String BOOK_PAGES = "book_pages";

        public final static String BOOK_YEAR = "book_year";

        public final static String BOOK_RATE = "book_rate";

        public final static String BOOK_DESCRIPTION = "book_description";

        public final static String BOOK_PRICE = "book_price";

        public final static String BOOK_IMAGE = "book_image";
    }
}