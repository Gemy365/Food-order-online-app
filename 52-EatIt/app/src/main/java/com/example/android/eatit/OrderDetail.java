package com.example.android.eatit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.android.eatit.Common.Common;

public class OrderDetail extends AppCompatActivity {
    TextView orderId, orderPhone,  orderTotal, orderAddress, orderClock;

    String orderIdValue = "";

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView lstFoods;

    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //      orderId = (TextView) findViewById(R.id.order_id);
        orderPhone = (TextView) findViewById(R.id.order_phone);
        orderTotal = (TextView) findViewById(R.id.order_total);
        orderAddress = (TextView) findViewById(R.id.order_address);
        orderClock = (TextView) findViewById(R.id.order_clock);

        // Load Menu.
        // To Get Image One By One.
        lstFoods = (RecyclerView) findViewById(R.id.list_foods);
        // Make All Has The Same Size.
        lstFoods.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_menu As A ListView.
        lstFoods.setLayoutManager(layoutManager);

        // Get CategoryId From Intent, It's True When You Click On Image On Menu.
        if (getIntent() != null)
            // Store ID Into OrderId.
            orderIdValue = getIntent().getStringExtra("OrderId");

        // Set Values.
        //orderId.setText(orderIdValue);
        orderPhone.setText(Common.currentRequest.getPhone());
        orderTotal.setText(Common.currentRequest.getTotal());
        orderAddress.setText(Common.currentRequest.getAddress());
        orderClock.setText(Common.currentRequest.getClock());

        // List Of Orders With Details Of Each Order.
        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());

        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        lstFoods.setAdapter(adapter);

    }
}
