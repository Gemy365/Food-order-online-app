package com.example.android.eatit;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Get Info From Food Detail And Set It Into cart_layout.xml.
// This Class As All Classes In ViewHolder Package.
class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    // Call From onCreateViewHolder Method.
    public CartViewHolder(View itemView) {
        super(itemView);

        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView) itemView.findViewById(R.id.cart_item_count);

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
// End Of CartViewHolder & Start New Class Called CartAdapter.
// Get List Order From Cart.java
public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{
    private List<Order> listData = new ArrayList<>();
    private Context context;

    // Get Array Of List Order To Adapt It.
    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    // Store All Of This Informations Into R.layout.cart_layout To Appear It.
    // This Method As Adapter Class In My Azkar Program.
    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        // Create new CartViewHolder Method To hold New Info Every Time.
        return new CartViewHolder(itemView);
    }

    // Override Method To Order The List Of Order Down Together By Position.
    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        // Get Position Of Customer's Order From Order Class.
        Order order = listData.get(position);

        // By Position Get Quantity And Create New TextDrawable And Put Number Of Quantity Into It.
        TextDrawable drawable = TextDrawable.builder().buildRound("" + order.getQuantity(), Color.BLACK);

        // Set Image By drawable.
        holder.img_cart_count.setImageDrawable(drawable);

        // Name Of Food By Position Every Time.
        holder.txt_cart_name.setText(order.getProductName());

        // Language Of Price.
        Locale locale = new Locale("en", "US");

        // Format Of Price.
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

        // Set Text By Current Price.
        int price = (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        // Set Text By price.
        holder.txt_price.setText(numFormat.format(price));
    }

    // Number Of Orders.
    @Override
    public int getItemCount() {
        return listData.size();
    }
}
