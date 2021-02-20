package ua.kpi.comsys.io8227.jackshen.gallery;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Picture fragment of application
 */
public final class PictureContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */

    static final String CONTENT_AUTHORITY = "ua.kpi.comsys.io8227.jackshen.gallery";

    static final String PATH_IMAGES = "images";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PictureContract() {}

    /**
     * Inner class that defines constant values for the pictures database table.
     * Each entry in the table represents a single item.
     */
    public static abstract class PictureEntry implements BaseColumns {

        /** Complete CONTENT_URI */
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_IMAGES);

        /** The MIME type of the {@link #CONTENT_URI} for a list of pictures. */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        /** The MIME type of the {@link #CONTENT_URI} for a single picture. */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        // Name of database table
        public final static String TABLE_NAME = "images";

        public final static String IMAGE_ID = BaseColumns._ID;

        public final static String IMAGE_NET_ID = "image_net_id";

        public final static String IMAGE_URL = "image_url";

        public final static String IMAGE_TAGS = "image_tags";

        public final static String IMAGE_WIDTH = "image_width";

        public final static String IMAGE_HEIGHT = "image_height";

        public final static String IMAGE_VIEWS = "image_views";

        public final static String IMAGE_DOWNLOADS = "image_downloads";

        public final static String IMAGE_FAVORITES = "image_favorites";

        public final static String IMAGE_LIKES = "image_likes";

        public final static String USER_NAME = "user_name";

        public final static String USER_IMAGE = "user_image";
    }
}