package com.example.android.eatit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Food;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.Model.Request;
import com.example.android.eatit.ViewHolder.MenuViewHolder;
import com.example.android.eatit.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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

    // To Make All App Like As The Same Font Make Sure To Put This Code Before onCreate Method
    // In Every Activity Press Ctrl + O.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To Make All App Like As The Same Font Make Sure To Put This Code Before setContentView Method
        // In Every Activity.
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

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
        // Create Query To Get Phone For Orders.
        Query getOrderByUser =  requests.orderByChild("phone").equalTo(phone);

        // Convert From FirebaseUI 1.2.0 To FirebaseUI 3.2.2.
        FirebaseRecyclerOptions<Request> orderOptions = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser, Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOptions) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAdress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
            }
        };
        // Start Display Item For Newest FirebaseUI.
        adapter.startListening();
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recycler_order.setAdapter(adapter);
    }

}
