package com.example.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.R;

// Using For FireBaseUI.
public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // For Name & Image In Food.
    public TextView txtFoodName;
    public ImageView imgFoodView;

    // Create itemClickListener Variable To Inherit From Own ItemClickListener InterFace.
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        // Extends From RecyclerView.ViewHolder.
        super(itemView);

        // Get IDs To Make Card View Of Images.
        txtFoodName = (TextView) itemView.findViewById(R.id.food_name);
        imgFoodView = (ImageView) itemView.findViewById(R.id.food_image);

        // Listen When Click On Image.
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Inherit Constructor From Own ItemClickListener InterFace.
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }
}
