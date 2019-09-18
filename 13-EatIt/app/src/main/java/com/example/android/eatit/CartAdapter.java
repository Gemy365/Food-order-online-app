package com.example.android.eatit;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.android.eatit.Model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Get Info From Food Detail And Set It Into cart_layout.xml.
class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    public void setTxt_cart_name(TextView txt_cart_name){
        this.txt_cart_name = txt_cart_name;
    }

    // Call From onCreateViewHolder Method.
    public CartViewHolder(View itemView) {
        super(itemView);

        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView) itemView.findViewById(R.id.cart_item_count);
    }

    @Override
    public void onClick(View view) {

    }
}

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
        // By Position Get Quantity And Create New TextDrawable And Put Number Of Quantity Into It.
        TextDrawable drawable = TextDrawable.builder().buildRound("" + listData.get(position)
        .getQuantity(), Color.RED);

        // Set Image By drawable.
        holder.img_cart_count.setImageDrawable(drawable);

        // Language Of Price.
        Locale locale = new Locale("en", "US");

        // Format Of Price.
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

        // Set Text By Current Price.
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

        // Set Text By price.
        holder.txt_price.setText(numFormat.format(price));
    }

    // Number Of Orders.
    @Override
    public int getItemCount() {
        return listData.size();
    }
}
