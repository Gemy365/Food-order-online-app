package com.example.android.eatitserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Interface.ItemClickListener;
import com.example.android.eatitserver.Model.Request;
import com.example.android.eatitserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

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

    MaterialSpinner spinner;


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
        loadOrders();
    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests) {

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                // Set Info In Oder Of User.
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Commons.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAdress.setText(model.getAddress());

                // When Click On Order.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(!isLongClick){
                            Intent trackingIntent = new Intent(OrderStatus.this, TrackingOrder.class);

                            Commons.currentRequest = model;

                            startActivity(trackingIntent);
                        }else{

                            Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);

                            Commons.currentRequest = model;

                            orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());

                            startActivity(orderDetail);
                        }

                    }
                });
            }
        };
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recycler_order.setAdapter(adapter);

    }

    // When Long Press On Image To Appear The Options.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // If It Equals Update.
        if (item.getTitle().equals(Commons.UPDATE)) {
            // Call showUpdateDialog Method
            // Take Two Prams [Key Of Ref Into DataBase By Order, Value Into Request Class].
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        // If It Equals Delete.
        else if (item.getTitle().equals(Commons.DELETE)) {
            // Call deleteCategory Method
            // Take One Pram [Key Of Ref Into DataBase By Order].
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    // To Delete Order.
    private void deleteOrder(String key) {
        // Get Value From DataBase And Remove It.
        requests.child(key).removeValue();
        Toast.makeText(this, "Order Deleted..!!", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Request item) {
        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("Update Order");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Please choose status ");

        // Adapt Alert Dialog As My Azkar App.
        LayoutInflater inflater = this.getLayoutInflater();

        // Get LayOut By ID.
        View updateOrderLayout = inflater.inflate(R.layout.update_order_layout, null);

        // Get Spinner By ID.
        spinner = (MaterialSpinner) updateOrderLayout.findViewById(R.id.status_spinner);

        // Set List For This Spinner.
        spinner.setItems("Placed", "On my way", "Shipped");

        // Add new View To Alert Dialog.
        alertDialog.setView(updateOrderLayout);

        // Make Buttons For Alert Dialog [Yes , No].
        // When Press On YES.
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Change Status By Index Of Item Into Spinner.
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                // Change Info Into DataBase.
                requests.child(key).setValue(item);
                // Close Alert Dialog.
                dialogInterface.dismiss();
            }
        });

        // When Press On NO.
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close Alert Dialog.
                dialogInterface.dismiss();
            }
        });
        // After Init All Of This, Show The Alert Dialog.
        alertDialog.show();
    }


}