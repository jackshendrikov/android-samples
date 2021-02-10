package ua.kpi.comsys.io8227.jackshen;

/** An {@link Book} object contains information related to a single book. */

class Book {

    /** Title of the book */
    private String mTitle;

    /** Subtitle of the book */
    private String mSubtitle;

    /** ISBN of the book*/
    private String mIsbn;

    /** Retail price of the book */
    private String mPrice;

    /** Cover of the book */
    private String mImageUrl;

    /**
     * Create book object
     *
     * @param title    - title of the book
     * @param subtitle - subtitle of the book
     * @param isbn     - isbn number of the book
     * @param price    - retail price of the book
     * @param imageUrl - the URL to find cover of the book
     */
    Book(String title, String subtitle, String isbn, String price, String imageUrl) {
        this.mTitle = title;
        this.mSubtitle = subtitle;
        this.mIsbn = isbn;
        this.mPrice = price;
        this.mImageUrl = imageUrl;
    }

    /**
     * Return the title information of the book
     *
     * @return the title of the book
     */
    String getTitle() {
        return mTitle;
    }

    /** Return the subtitle of the book */
    String getSubtitle() {
        return mSubtitle;
    }

    /** Return the ISBN number of the book */
    String getISBN() {
        return mIsbn;
    }

    /** Return the retail price of the book */
    String getPrice() {
        return mPrice;
    }

    /** Return the URL to find cover of the book */
    String getImageUrl() {
        return mImageUrl;
    }
}