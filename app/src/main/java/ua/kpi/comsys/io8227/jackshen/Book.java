package ua.kpi.comsys.io8227.jackshen;

import java.io.Serializable;

/**
 * An {@link Book} object contains information related to a single book.
 *
 * We need to implement the Serializable interface so then we can pass object instances in the
 * intent extra using the putExtra(Serializable..) variant of the Intent#putExtra() method.
 */

class Book implements Serializable {

    /** Title of the book */
    private String mTitle;

    /** Subtitle of the book */
    private String mSubtitle;

    /** Authors of the book */
    private String mAuthors;

    /** Publisher of the book */
    private String mPublisher;

    /** ISBN of the book */
    private String mIsbn;

    /** Number of pages in the book */
    private String mPages;

    /** Book publication year */
    private String mYear;

    /** Rating of the book */
    private String mRate;

    /** Description of the book */
    private String mDescription;

    /** Retail price of the book */
    private String mPrice;

    /** Cover of the book */
    private String mImageUrl;

    /**
     * Create book object
     *
     * @param title       - title of the book
     * @param subtitle    - subtitle of the book
     * @param authors     - authors of the book
     * @param publisher   - publisher of the book
     * @param isbn        - isbn number of the book
     * @param pages       - number of pages in the book
     * @param year        - book publication year
     * @param rate        - average rating of the book
     * @param description - description of the book
     * @param price       - retail price of the book
     * @param imageUrl    - the URL to find cover of the book
     */
    Book(String title, String subtitle, String authors, String publisher, String isbn,
         String pages, String year, String rate, String description, String price, String imageUrl) {
        this.mTitle = title;
        this.mSubtitle = subtitle;
        this.mAuthors = authors;
        this.mPublisher = publisher;
        this.mIsbn = isbn;
        this.mPages = pages;
        this.mYear = year;
        this.mRate = rate;
        this.mDescription = description;
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

    /** Return the authors of the book */
    String getAuthor() {
        return mAuthors;
    }

    /** Return the publisher of the book */
    String getPublisher() {
        return mPublisher;
    }

    /** Return the ISBN number of the book */
    String getISBN() { return mIsbn; }

    /** Return number of pages in the book */
    String getPages() { return mPages; }

    /** Return book publication year */
    String getYear() { return mYear; }

    /** Return average rating of the book */
    String getRate() { return mRate; }

    /** Return description of the book */
    String getDescription() { return mDescription; }

    /** Return the retail price of the book */
    String getPrice() { return mPrice; }

    /** Return the URL to find cover of the book */
    String getImageUrl() { return mImageUrl; }
}