package com.example.android.eatit;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Interface.RecyclerItemTouchHelperListener;
import com.example.android.eatit.Model.MyResponse;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.Model.Request;
import com.example.android.eatit.Model.Sender;
import com.example.android.eatit.Model.Token;
import com.example.android.eatit.Remote.APIService;
import com.example.android.eatit.Remote.IGoogleService;
import com.example.android.eatit.ViewHolder.CartAdapter;
import com.example.android.eatit.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.example.android.eatit.Model.Notification;
import com.rey.material.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

//public class Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, LocationListener, RecyclerItemTouchHelperListener {

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

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

    // For Place.
    String address;

    // For Place Too.
    Place shipingAddress;

    RelativeLayout actCartLayout;

    // All Of This For Location.
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // All Of This For Location Too.
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google Map API Retrofit.
    IGoogleService mGoogleMapService;


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

//        // Init Google Map.
//        mGoogleMapService = Common.getGoogleMapAPI();
//
//        //Check if NOT accept Permissions from device To Get Location Of Device.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Send these Permissions to Override onRequestPermissionsResult Method to ask the program,
//            // to send the result as permission accepted or permission denied.
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            }, LOCATION_PERMISSION_REQUEST);
//        } else {
//            if (checkPlayServices()) // If Have Play Service On Device.
//            {
//                buildGoogledApiClient();
//                createLocationRequest();
//            }
//        }

        // Get Views By ID.
        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);
        actCartLayout = (RelativeLayout) findViewById(R.id.act_cart_layout);

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

        // Swipe Order To Delete It From Cart.
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        Toast.makeText(Cart.this, "ممكن تلغي أوردر بأنك تسحبه شمال او تثبت أيدك عليه", Toast.LENGTH_SHORT).show();

        // When Click Of Btn Place.
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If Cart
                if (cart.size() > 0)
                    // Call showAlertDialog Method.
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "فين طلبك..!!", Toast.LENGTH_LONG).show();
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

        //final MaterialEditText edtAddress = (MaterialEditText) orderAddressComment.findViewById(R.id.edt_address);

//        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        // Hide Search Icon Before Fragment.
//        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
//
//        // Set Hint For AutoComplete Edit Text.
//        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
//                .setHint("Enter your address");
//
//        // Set Text Size.
//        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
//                .setTextSize(14);
//
//        // Get Address From Place AutoComplete.
//        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                shipingAddress = place;
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.e("ERROR", status.getStatusMessage());
//            }
//        });


        final MaterialEditText edtComment = (MaterialEditText) orderAddressComment.findViewById(R.id.edt_comment);

        // Radio For Location.
//        final CheckBox chkShipToAddress = (CheckBox) orderAddressComment.findViewById(R.id.rdi_ship_to_address);
//
//        // When Choose CheckBox.
//        chkShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // Ship To This Address.
//                if (true) {
//                    Log.d("LANLAG",  mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
//                    // We Need Make Our Location Like This. [https://maps.googleapis.com/maps/api/geocode/json?latlng=29.9643087,30.9012286&sensor=false].
//                    mGoogleMapService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&sensor=false"))
//                            .enqueue(new Callback<String>() {
//                                @Override
//                                public void onResponse(Call<String> call, Response<String> response) {
//                                    // If Fetch API Ok.
//                                    try {
//
//                                        JSONObject jsonObject = new JSONObject(response.body().toString());
//
//                                        JSONArray resultArray = jsonObject.getJSONArray("results");
//
//                                        // 0 Cause We Need [formatted_address] & This In Index 0
//                                        // To Make Sure From That Enter This Site [http://jsoneditoronline.org/]
//                                        // And Take Json From Previous Site And See Index 0.
//                                        JSONObject firstObject = resultArray.getJSONObject(0);
//
//                                        address = firstObject.getString("formatted_address");
//
//                                        // Set This Address To edtAddress.
//                                 //       ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
//                                  //              .setText(address);
//
//                                        Toast.makeText(Cart.this, "تم تحديد مكانك" , Toast.LENGTH_SHORT).show();
//
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<String> call, Throwable t) {
//                                    Toast.makeText(Cart.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//            }
//        });

        // Set This View.
        alertDialog.setView(orderAddressComment);
        // Add Icon To Alert Dialog.
        alertDialog.setIcon(R.drawable.ic_shopping_black);

        // Make Buttons For Alert Dialog [Yes , No].
        // When Press On YES.
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // If User Select Address From Placee Fragment, Just Use It.
                // If User Select Ship To This Address , Get Address From Location And Use It.
                // If User Select Home Address , Get Home Address From Profile And Use It.

                // If CheckBox Is Not Checked.
//                if(!chkShipToAddress.isChecked()) {
//
//                    if (shipingAddress != null)
//                        // Get Address Of User.
//                        address = shipingAddress.getAddress().toString();

                // If User Enter His Address.
                if (!edtComment.getText().toString().equals("")) {
                    Toast.makeText(Cart.this, "عرفنا مكانك و دقايق و نكون عندك", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Cart.this, "مكانك هيساعدنا نوصلك", Toast.LENGTH_LONG).show();

                    // Fix Crash Fragment.
//                        getFragmentManager().beginTransaction()
//                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
//                                .commit();

                    return;
                }
                //    }


                // Send This Info To Constructor Of Request.
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        edtComment.getText().toString(),
                        txtTotalPrice.getText().toString(),
  //                      String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                        cart
                );

                String rndmOrderNum = String.valueOf(System.currentTimeMillis());
                // Submit To FireBase.
                // Use System.currentMilli To Key.
                requests.child(rndmOrderNum).setValue(request);

                // Delete Cart To Make It Clear From Old Orders.
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

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

                // Fix Crash Fragment.
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
//                        .commit();
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
                    Notification notification = new Notification("Gemy", "أوردر جديد");

                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    // Work Only When All Is Right.
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {

                                            Toast.makeText(Cart.this, "شكرآ ليك..جاري تجهييز طلبك", Toast.LENGTH_SHORT).show();
                                            // Out From Fire Base.
                                            finish();
                                        } else
                                            Toast.makeText(Cart.this, "خطأ !!", Toast.LENGTH_SHORT).show();
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
        cart = new Database(this).getCarts(Common.currentUser.getPhone());

        // Call Constructor Of CartAdapter.
        adapter = new CartAdapter(cart, this);

        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();

        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerView.setAdapter(adapter);

        // Calculate Total Price.
        double total = 0;

        // For Each To Calculate Total Price.
        for (Order order : cart)
            // Multiple Between Price & Quantity.
            total += (Double.parseDouble(order.getPrice())) * (Double.parseDouble(order.getQuantity()));

        // Language Of Price.
        Locale locale = new Locale("ar", "EG");

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
        new Database(this).cleanCart(Common.currentUser.getPhone());

        // Update New Data From List<Order> To SQLite.
        for (Order item : cart)
            new Database(this).addToCart(item);

        // Refresh.
        loadListOrder();
    }

//    /**
//     * Ctrl + O
//     * Override Method Calling Automatic When we call requestPermission Method Cause,
//     * requestPermission Method Has "ActivityCompat.requestPermissions" That's as Calling,
//     * the Override onRequestPermissionsResult Method.
//     * Check If The Permissions are in Manifest.xml or not.
//     **/
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case LOCATION_PERMISSION_REQUEST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (checkPlayServices()) {
//                        buildGoogledApiClient();
//                        createLocationRequest();
//                    }
//                }
//                break;
//        }
//    }
//
//    // Options To Build Google API For Location.
//    protected synchronized void buildGoogledApiClient() {
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();
//
//        mGoogleApiClient.connect();
//    }
//
//    // Options To Make Request For Location.
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//    }
//
//    // Check If Have Play Service.
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//
//        if (resultCode != ConnectionResult.SUCCESS) {
//
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        displayLocation();
//        startLocationUpdates();
//    }
//
//    // Display Where's Location.
//    private void displayLocation() {
//        //Check if NOT accept Permissions from device.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null)
//            Log.d("LOCATION", "Your location : " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
//        else
//            Log.d("LOCATION", "Couldn't get your location");
//    }
//
//    // Make Update If Location Has Changed.
//    private void startLocationUpdates() {
//        //Check if NOT accept Permissions from device.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//    }
//
//    // Override For implementation Method.
//    @Override
//    public void onConnectionSuspended(int i) {
//        mGoogleApiClient.connect();
//    }
//
//    // Override For implementation Method.
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//
//    // Override For implementation Method.
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        displayLocation();
//
//    }

    // For Delete Item By Swipe From Cart.
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);

            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone());

            //Update Price.
            // Calculate Total Price.
            double total = 0;

            // Get New Quantity From DataBase.
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            // For Each To Calculate Total Price.
            for (Order item : orders)
                // Multiple Between Price & Quantity.
                total += (Double.parseDouble(item.getPrice())) * (Double.parseDouble(item.getQuantity()));

            // Language Of Price.
            Locale locale = new Locale("ar", "EG");

            // Format Of Price.
            NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

            // Set Text By Current Price.
            txtTotalPrice.setText(numFormat.format(total));

            // Make Snake Bar.
            Snackbar snackbar = Snackbar.make(actCartLayout, name + " removed from cart", Snackbar.LENGTH_LONG);
            snackbar.setAction("أسترجاع الأوردر", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteIndex, deleteItem);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    //Update Price.
                    // Calculate Total Price.
                    double total = 0;

                    // Get New Quantity From DataBase.
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    // For Each To Calculate Total Price.
                    for (Order item : orders)
                        // Multiple Between Price & Quantity.
                        total += (Double.parseDouble(item.getPrice())) * (Double.parseDouble(item.getQuantity()));

                    // Language Of Price.
                    Locale locale = new Locale("ar", "EG");

                    // Format Of Price.
                    NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);

                    // Set Text By Current Price.
                    txtTotalPrice.setText(numFormat.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
