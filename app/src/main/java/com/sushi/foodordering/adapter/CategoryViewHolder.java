package com.sushi.foodordering.adapter;


import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sushi.foodordering.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder{

    public TextView categoryName;
    public ImageView categoryImage;
    public View mItemView;


    public CategoryViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        categoryName = (TextView)itemView.findViewById(R.id.category_name);
        categoryImage = (ImageView)itemView.findViewById(R.id.category_image);
    }
}
