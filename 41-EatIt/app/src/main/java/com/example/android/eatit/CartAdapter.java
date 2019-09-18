package com.example.android.eatit;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Model.Order;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Get Info From Food Detail And Set It Into cart_layout.xml.
// This Class As All Classes In ViewHolder Package.
class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView txtCartName, txtPrice;
    public ElegantNumberButton btnQuantity;
    public ImageView CartImage;

    // Call From onCreateViewHolder Method.
    public CartViewHolder(View itemView) {
        super(itemView);

        txtCartName = (TextView) itemView.findViewById(R.id.cart_item_name);
        txtPrice = (TextView) itemView.findViewById(R.id.cart_item_price);
        btnQuantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
        CartImage = (ImageView) itemView.findViewById(R.id.cart_img);

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
    private Cart cart;

    // Get Array Of List Order To Adapt It.
    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    // Store All Of This Informations Into R.layout.cart_layout To Appear It.
    // This Method As Adapter Class In My Azkar Program.
    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        // Create new CartViewHolder Method To hold New Info Every Time.
        return new CartViewHolder(itemView);
    }

    // Override Method To Order The List Of Order Down Together By Position.
    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
        // Get Position Of Customer's Order From Order Class.
        final Order order = listData.get(position);
//        // By Position Get Quantity And Create New TextDrawable And Put Number Of Quantity Into It.
//        TextDrawable drawable = TextDrawable.builder().buildRound("" + order.getQuantity(), Color.BLACK);
//
//        // Set Image By drawable.
//        holder.img_cart_count.setImageDrawable(drawable);

        // Get Image Of Food And Set It To Be Image Of Cart.
        Picasso.with(cart.getBaseContext()).load(order.getImage())
                .resize(70, 70)
                .centerCrop().into(holder.CartImage);

        // Set Number Of Btn To Freeze Quantity That User Choose It.
        holder.btnQuantity.setNumber(listData.get(position).getQuantity());
        // When Click On Btn Quantity From Cart In Home Activity [Order Page].
        holder.btnQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                // Set New Quantity When Click On Btn.
                order.setQuantity(String.valueOf(newValue));

                // Send New Value To DataBase To Make Update.
                new Database(cart).updateCart(order);

                //Update Price When Click On Btn.
                // Calculate Total Price.
                int total = 0;

                // Get New Quantity From DataBase.
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                // For Each To Calculate Total Price.
                for (Order item : orders)
                    // Multiple Between Price & Quantity.
                    total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(item.getQuantity()));

                // Language Of Price.
                Locale locale = new Locale("en", "US");

                // Format Of Price.
                NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

                // Set Text By Current Price.
                cart.txtTotalPrice.setText(numFormat.format(total));
            }
        });



        // Name Of Food By Position Every Time.
        holder.txtCartName.setText(order.getProductName());

        // Language Of Price.
        Locale locale = new Locale("en", "US");

        // Format Of Price.
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

        // Set Text By Current Price.
        int price = (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        // Set Text By price.
        holder.txtPrice.setText(numFormat.format(price));
    }

    // Number Of Orders.
    @Override
    public int getItemCount() {
        return listData.size();
    }
}
