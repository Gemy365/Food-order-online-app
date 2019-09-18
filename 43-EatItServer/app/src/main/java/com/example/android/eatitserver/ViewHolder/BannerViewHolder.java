package com.example.android.eatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Interface.ItemClickListener;
import com.example.android.eatitserver.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    // For Name & Image In Menu.
    public TextView bannerName;
    public ImageView bannerImg;


    public BannerViewHolder(View itemView) {
        // Extends From RecyclerView.ViewHolder.
        super(itemView);

        // Get IDs To Make Card View Of Images.
        bannerName = (TextView) itemView.findViewById(R.id.banner_name);
        bannerImg = (ImageView) itemView.findViewById(R.id.banner_image);

        // Listen When Long Press On Image To Appear The Options.
        itemView.setOnCreateContextMenuListener(this);
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
