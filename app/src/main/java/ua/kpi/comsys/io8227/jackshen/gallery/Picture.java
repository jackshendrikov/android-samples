package ua.kpi.comsys.io8227.jackshen.gallery;

import java.io.Serializable;

public class Picture implements Serializable {
    /** URL of the image */
    private String mImageUrl;

    /** Width of the image */
    private String mImageWidth;

    /** Height of the image */
    private String mImageHeight;

    /** Number of image views */
    private String mImageViews;

    /** Number of image downloads */
    private String mImageDownloads;

    /** Number of image saves */
    private String mImageFavorites;

    /** Number of image likes */
    private String mImageLikes;

    /** User name */
    private String mUserName;

    /** User image */
    private String mUserImage;


    /**
     * Create picture object
     *
     * @param imageUrl       - the URL of the image
     * @param imageWidth     - width of the image
     * @param imageHeight    - height of the image
     * @param imageViews     - number of image views
     * @param imageDownloads - number of image downloads
     * @param imageFavorites - number of image saves
     * @param imageLikes     - number of image likes
     * @param userName       - user name
     * @param userImage      - user image
     */
    Picture(String imageUrl, String imageWidth, String imageHeight, String imageViews,
            String imageDownloads, String imageFavorites, String imageLikes, String userName,
            String userImage) {
        this.mImageUrl = imageUrl;
        this.mImageWidth = imageWidth;
        this.mImageHeight = imageHeight;
        this.mImageViews = imageViews;
        this.mImageDownloads = imageDownloads;
        this.mImageFavorites = imageFavorites;
        this.mImageLikes = imageLikes;
        this.mUserName = userName;
        this.mUserImage = userImage;

    }

    /** Return the URL of the image */
    String getImageUrl() { return mImageUrl; }

    /** Return width of the image */
    String getImageWidth() { return mImageWidth; }

    /** Return height of the image */
    String getImageHeight() { return mImageHeight; }

    /** Return number of image views */
    String getViews() { return mImageViews; }

    /** Return number of image downloads */
    String getDownloads() { return mImageDownloads; }

    /** Return number of image saves */
    String getFavorites() { return mImageFavorites; }

    /** Return number of image likes */
    String getLikes() { return mImageLikes; }

    /** Return user names */
    String getUser() { return mUserName; }

    /** Return user image */
    String getUserImage() { return mUserImage; }


}
