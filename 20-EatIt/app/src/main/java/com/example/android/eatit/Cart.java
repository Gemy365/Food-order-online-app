package com.example.android.eatit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.Model.Request;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerView;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    // For Data Base.
    FirebaseDatabase database;
    // Init DataBase.
    DatabaseReference requests;

    // To Add Total Of Price.
    TextView txtTotalPrice;

    // To Allow User Type His Place.
    FButton btnPlace;

    // List Of Order.
    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    // Remember To Change System.currentTimeMillis() [As A Random Key] To Your Own Key [As A Order Of Numbers].
    int keyOfRequest = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Request To Store Orders Of Users.
        requests = database.getReference("Request");

        // Load Menu.
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        // Make All Has The Same Size.
        recyclerView.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recyclerView As A ListView.
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);

        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);

        // When Click Of Btn Place.
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If Cart
                if(cart.size() > 0)
                    // Call showAlertDialog Method.
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty..", Toast.LENGTH_SHORT).show();
            }
        });
        // Call loadListOrder Method.
        loadListOrder();
    }

    // showAlertDialog Method Called When Pressed On Btn Place.
    private void showAlertDialog() {
        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("One more stop!");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Enter your address: ");

        LayoutInflater inflater = this.getLayoutInflater();

        View orderAddressComment = inflater.inflate(R.layout.order_adress_comment, null);

        final MaterialEditText edtAddress = (MaterialEditText) orderAddressComment.findViewById(R.id.edt_address);

        final MaterialEditText edtComment = (MaterialEditText) orderAddressComment.findViewById(R.id.edt_comment);

        alertDialog.setView(orderAddressComment);
        // Add Icon To Alert Dialog.
        alertDialog.setIcon(R.drawable.ic_shopping_black);

        // Make Buttons For Alert Dialog [Yes , No].
        // When Press On YES.
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Send This Info To Constructor Of Request.
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        edtComment.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );
                // Submit To FireBase.
                // Use System.currentMilli To Key.
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                // Delete Cart To Make It Clear From Old Orders.
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                // Out From Fire Base.
                finish();
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

    // When Call loadListOrder Method.
    private void loadListOrder() {
        // Call Constructor Of Database To Make New cart.
        cart = new Database(this).getCarts();

        // Call Constructor Of CartAdapter.
        adapter = new CartAdapter(cart, this);

        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();

        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerView.setAdapter(adapter);

        // Calculate Total Price.
        int total = 0;

        // For Each To Calculate Total Price.
        for (Order order : cart)
            // Multiple Between Price & Quantity.
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        // Language Of Price.
        Locale locale = new Locale("en", "US");

        // Format Of Price.
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

        // Set Text By Current Price.
        txtTotalPrice.setText(numFormat.format(total));
    }

    // If Title Equals Delete, So Delete Cart Order Of User.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            // Call deleteCart Method & Delete Item By Position.
            deleteCart(item.getOrder());

        return super.onContextItemSelected(item);
    }

    // When Called deleteCart Method.
    private void deleteCart(int position) {
        // Remove Item At List<Order> By Position.
        cart.remove(position);

        // Delete Old Data From SQLite DataBase.
        new Database(this).cleanCart();

        // Update New Data From List<Order> To SQLite.
        for(Order item : cart)
            new Database(this).addToCart(item);

        // Refresh.
        loadListOrder();
    }
}
