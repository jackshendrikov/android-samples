package ua.kpi.comsys.io8227.jackshen;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


class PictureViewHolder extends RecyclerView.ViewHolder {

    ImageView mImageView;

    PictureViewHolder(View itemView) {
        super(itemView);

        mImageView = itemView.findViewById(R.id.image_result);
    }
}