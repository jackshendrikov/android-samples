package ua.kpi.comsys.io8227.jackshen.gallery;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ua.kpi.comsys.io8227.jackshen.R;


class PictureViewHolder extends RecyclerView.ViewHolder {

    ImageView mImageView;
    CardView mCardView;

    PictureViewHolder(View itemView) {
        super(itemView);

        mImageView = itemView.findViewById(R.id.image_result);
        mCardView = itemView.findViewById(R.id.image_card);
    }
}