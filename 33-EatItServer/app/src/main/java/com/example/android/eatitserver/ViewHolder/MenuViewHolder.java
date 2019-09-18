package com.example.android.eatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Interface.ItemClickListener;
import com.example.android.eatitserver.R;

// Using For FireBaseUI.
public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{
    // For Name & Image In Menu.
    public TextView txtMenuName;
    public ImageView imgMenuView;

    // Create itemClickListener Variable To Inherit From Own ItemClickListener InterFace.
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        // Extends From RecyclerView.ViewHolder.
        super(itemView);

        // Get IDs To Make Card View Of Images.
        txtMenuName = (TextView) itemView.findViewById(R.id.menu_name);
        imgMenuView = (ImageView) itemView.findViewById(R.id.menu_image);

        // Listen When Long Press On Image To Appear The Options.
        itemView.setOnCreateContextMenuListener(this);

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

    // Called When Long Press On Image To Appear The Options.
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the option");

        // [0 , 0] Cause Update Above Of Delete.
        contextMenu.add(0, 0, getAdapterPosition(), Commons.UPDATE);

        // [0 , 1] Cause Delete Below Update.
        contextMenu.add(0, 1, getAdapterPosition(), Commons.DELETE);
    }
}
