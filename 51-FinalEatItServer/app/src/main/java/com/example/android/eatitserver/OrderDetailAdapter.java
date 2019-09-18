package com.example.android.eatitserver;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.eatitserver.Model.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name, quantity, price, total, discount;

    public MyViewHolder(View itemView) {
        super(itemView);

        // Get Views By ID.
        name = (TextView) itemView.findViewById(R.id.product_name);
        quantity = (TextView) itemView.findViewById(R.id.product_quantity);
        price = (TextView) itemView.findViewById(R.id.product_price);
        total = (TextView) itemView.findViewById(R.id.product_total);
        discount = (TextView) itemView.findViewById(R.id.product_discount);
    }
}
public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrders;

    Order order;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        order = myOrders.get(position);

        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));
        holder.price.setText(String.format("Price : %s", order.getPrice()));
        holder.total.setText(String.format("Total : %s", getTotal()));
        holder.discount.setText(String.format("Discount : %s", order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }

    // Total Of One Order.
    public String getTotal(){
        // Calculate Total Price.
        double totalOrder = 0;

        // Multiple Between Price & Quantity.
        totalOrder = Double.parseDouble(order.getPrice()) * Double.parseDouble(order.getQuantity());


        // Return Total English Language By 2 decimal places.
        return String.valueOf(String.format (Locale.ENGLISH, "%.2f", totalOrder));
    }

}
