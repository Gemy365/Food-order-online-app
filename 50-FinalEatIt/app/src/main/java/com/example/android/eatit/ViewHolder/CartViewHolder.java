package com.example.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.eatit.Common.Common;
import com.example.android.eatit.R;

// Get Info From Food Detail And Set It Into cart_layout.xml.
// This Class As All Classes In ViewHolder Package.
public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txtCartName, txtPrice;
    public ElegantNumberButton btnQuantity;
    public ImageView CartImage;

    public RelativeLayout viewBackground;
    public LinearLayout viewForeBackground;

    // Call From onCreateViewHolder Method.
    public CartViewHolder(View itemView) {
        super(itemView);

        txtCartName = (TextView) itemView.findViewById(R.id.cart_item_name);
        txtPrice = (TextView) itemView.findViewById(R.id.cart_item_price);
        btnQuantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
        CartImage = (ImageView) itemView.findViewById(R.id.cart_img);

        viewBackground = (RelativeLayout) itemView.findViewById(R.id.view_background);
        viewForeBackground = (LinearLayout) itemView.findViewById(R.id.view_foreground);

        // Listen When Long Press On Image To Appear The Options.
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    // Method Long Press To Allow User Delete Item.
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        // [0 , 0] Cause Delete In First.
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}