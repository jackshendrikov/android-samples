package ua.kpi.comsys.io8227.jackshen;

import java.io.Serializable;

public class Picture implements Serializable {
    /** URL of image */
    private String mImageUrl;


    Picture(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    /** Return the URL of the image */
    String getImageUrl() { return mImageUrl; }

}
