package com.example.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAdress, txtOrderComment;

    // Create itemClickListener Variable To Inherit From Own ItemClickListener InterFace.
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        // Extends From RecyclerView.ViewHolder.
        super(itemView);

        // Get IDs To Make View Of Texts.
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderAdress = (TextView) itemView.findViewById(R.id.order_adress);
        txtOrderComment = (TextView) itemView.findViewById(R.id.order_comment);

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
