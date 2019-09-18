package com.example.android.eatitserver.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Interface.ItemClickListener;
import com.example.android.eatitserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAdress;

    // Create itemClickListener Variable To Inherit From Own ItemClickListener InterFace.
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        // Extends From RecyclerView.ViewHolder.
        super(itemView);

        // Get IDs To Make View Of Texts.
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderAdress = (TextView) itemView.findViewById(R.id.order_address);

        // Listen When Long Press On Image To Appear The Options.
        itemView.setOnCreateContextMenuListener(this);

        // Listen When Click On Image.
        itemView.setOnClickListener(this);

        // Listen When Click On Image.
     //   itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Inherit Constructor From Own ItemClickListener InterFace.
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    // Called When Long Press On Image To Appear The Options.
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the option");

        // [0 , 0] Cause Update Above Of Delete.
        contextMenu.add(0, 0, getAdapterPosition(), Commons.UPDATE);

        // [0 , 1] Cause Delete Below Update.
        contextMenu.add(0, 1, getAdapterPosition(), Commons.DELETE);
    }

//    // Called When Long Click On Item.
//    @Override
//    public boolean onLongClick(View view) {
//        itemClickListener.onClick(view, getAdapterPosition(), true);
//        return true;
//    }
}
