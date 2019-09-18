package com.example.android.eatit;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Model.MyResponse;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.Model.Request;
import com.example.android.eatit.Model.Sender;
import com.example.android.eatit.Model.Token;
import com.example.android.eatit.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.example.android.eatit.Model.Notification;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerView;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    // For Data Base.
    FirebaseDatabase database;
    // Init DataBase.
    DatabaseReference requests;

    // To Add Total Of Price, public To Use It Into CartAdapter.
    public TextView txtTotalPrice;

    // To Allow User Type His Place.
    FButton btnPlace;

    // List Of Order.
    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    APIService mService;

    // Remember To Change System.currentTimeMillis() [As A Random Key] To Your Own Key [As A Order Of Numbers].
    int keyOfRequest = 1;

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

        setContentView(R.layout.activity_cart);

        // Get Views By ID.
        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);

        // Init Service.
        mService = Common.getFCMService();

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


        // When Click Of Btn Place.
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If Cart
                if (cart.size() > 0)
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

        // Make LayoutInflater To Adapt This Alert.
        LayoutInflater inflater = this.getLayoutInflater();

        // Make View Use Layout.
        View orderAddressComment = inflater.inflate(R.layout.order_adress_comment, null);

        final MaterialEditText edtAddress = (MaterialEditText) orderAddressComment.findViewById(R.id.edt_address);

        final MaterialEditText edtComment = (MaterialEditText) orderAddressComment.findViewById(R.id.edt_comment);

        // Set This View.
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

                String rndmOrderNum = String.valueOf(System.currentTimeMillis());
                // Submit To FireBase.
                // Use System.currentMilli To Key.
                requests.child(rndmOrderNum).setValue(request);

                // Delete Cart To Make It Clear From Old Orders.
                new Database(getBaseContext()).cleanCart();

                // Call sendNotificationOrder To Send Notification To Server.
                sendNotificationOrder(rndmOrderNum);

//                Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
//                // Out From Fire Base.
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

    private void sendNotificationOrder(final String rndmOrderNum) {
        DatabaseReference tokens = database.getReference("Token");

        // Get All Node Have serverToken Is True.
        Query data = tokens.orderByChild("serverToken").equalTo(true);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);

                    // Create Raw Payload To Send.
                    Notification notification = new Notification("Gemy", "You have new order " + rndmOrderNum);

                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    // Work Only When All Is Right.
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {

                                            Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                                            // Out From Fire Base.
                                            finish();
                                        } else
                                            Toast.makeText(Cart.this, "Failed !!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        if (item.getTitle().equals(Common.DELETE))
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
        for (Order item : cart)
            new Database(this).addToCart(item);

        // Refresh.
        loadListOrder();
    }
}
