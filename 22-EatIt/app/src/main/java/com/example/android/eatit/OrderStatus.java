package com.example.android.eatit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.Model.Request;
import com.example.android.eatit.ViewHolder.MenuViewHolder;
import com.example.android.eatit.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    // For Data Base.
    FirebaseDatabase database;
    // Get DataBase By Reference.
    DatabaseReference requests;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recycler_order;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;
    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Request For Orders.
        requests = database.getReference("Request");

        // Load Menu.
        // To Get Image One By One.
        recycler_order = (RecyclerView) findViewById(R.id.listOrders);
        // Make All Has The Same Size.
        recycler_order.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_menu As A ListView.
        recycler_order.setLayoutManager(layoutManager);

        // Call This Method To Adapter This List View To Appear Info On Device After Adapt It.
        // If We Start From Home Activity.
//        if(getIntent() == null) {
            loadOrders(Common.currentUser.getPhone());
//            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            // If We Come From Notifications When User Close The App.
//            loadOrders(getIntent().getStringExtra("userPhone"));
//            Toast.makeText(this, "NOT NULL " + getIntent().getStringExtra("userPhone"), Toast.LENGTH_SHORT).show();
//    }
    }

    // When Call loadOrders Method.
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)) {

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAdress.setText(model.getAddress());
                viewHolder.txtOrderComment.setText(model.getComment());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                    }
                });
            }
        };
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recycler_order.setAdapter(adapter);
    }


}
