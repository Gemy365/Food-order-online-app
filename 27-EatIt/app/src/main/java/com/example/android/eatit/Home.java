package com.example.android.eatit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Category;
import com.example.android.eatit.Model.Request;
import com.example.android.eatit.Model.Token;
import com.example.android.eatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // For Data Base.
    FirebaseDatabase database;
    // To Get Images Of Menu.
    DatabaseReference category;
    // Store Full Name For User.
    TextView txtFullName;

    // For Refresh Menu.
    SwipeRefreshLayout swipeRefreshLayout;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recycler_menu;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;
    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

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

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Make Title For This Menu Called [Menu].
        toolbar.setTitle("Menu");
        // Activate This Action Bar.
        setSupportActionBar(toolbar);

        // Get View By ID For Refresh View When Get Menu Down By Your Finger To Refresh.
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        // Set Colors For Refresh If The Refresh Need Long Time.
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Check If InterNet Available.
                if (Common.isConnectedToInternet(Home.this))
                    // Call This Method To Adapter This List View To Appear Info On Device After Adapt It.
                    loadMenu();
                    // If Not Available.
                else
                    Toast.makeText(Home.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
            }
        });

        // Defualt, Load For First Time.
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // Check If InterNet Available.
                if (Common.isConnectedToInternet(Home.this))
                    // Call This Method To Adapter This List View To Appear Info On Device After Adapt It.
                    loadMenu();
                    // If Not Available.
                else
                    Toast.makeText(Home.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
            }
        });

        // Init Paper To Save key-value, To Save User Name & Password To Remember User.
        Paper.init(this);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Category For Menu.
        category = database.getReference("Category");

        // When Click On fab Btn Into Main Menu.
        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Full Name For User.
        // 0 Cause The Name Store In Data Base In The First, So Get Index 0.
        View headerView = navigationView.getHeaderView(0);
        // Get Txt By ID.
        txtFullName = (TextView) headerView.findViewById(R.id.text_full_name);
        // Set Name Of User In Menu.
        txtFullName.setText(Common.currentUser.getName());

        // Load Menu.
        // To Get Image One By One.
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        // Make All Has The Same Size.
        recycler_menu.setHasFixedSize(true);

        // Init layoutManager As A ListView.
        // As My ListOfCall Program, Was Need To List View And Adapter.
  //      layoutManager = new LinearLayoutManager(this);
        // Set recycler_menu As A ListView.
     //   recycler_menu.setLayoutManager(layoutManager);

        // Set Menu As Grid 2 Columns.
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));



        // Register Service For Notifications, Create Unique Token When User Sign In App,
        // Each User Can Have One Unique Token, It's Make You Able To Send Notification For Him Only.
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    // When Called Create Unique Token When User Sign In App.
    private void updateToken(String tokenRefreshed) {
        // Init FireBase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Reference Or Name Of Our DataBase [Root], In This Case Called Token For Unique Token.
        DatabaseReference tokens = database.getReference("Token");

        // False Cause This Token Send From Client To Server.
        Token token = new Token(tokenRefreshed, false);

        // Make Phone Is Child Of Token Root And Set Value Inside This Phone.
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }


    // Call loadMenu Method To Adapt The Menu.
    private void loadMenu() {

        // Convert From FirebaseUI 1.2.0 To FirebaseUI 3.2.2.
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class).build();
        // Adapter Take Two Parameters. To Appear The Information On The Device.
        // As My ListOfCall Program, Was Need To List View And Adapter.
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }

            /**
             * @param viewHolder From MenuViewHolder Own Class.
             * @param model From Category Own Class.
             * @param position To Get Item By Position.
             */
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                // To Set Name Of Image Every Time.
                // model parameter Of Category Own Of This Method.
                viewHolder.txtMenuName.setText(model.getName());
                // To Load Image From Fire Base And Set It Into imgMenuView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgMenuView);

                // Call Own setItemClickListener Method When Click On Image.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Get CategoryId And Send To New Activity.
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        // Cause CategoryId is Key, So We Just Get Key Of This Item.
                        // putExtra[Key, Value].
                        // Send CategoryId To FoodList Activity.
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        // Start Activity.
                        startActivity(foodList);
                    }
                });
            }

        };
        // Start Display Item For Newest FirebaseUI.
        adapter.startListening();
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recycler_menu.setAdapter(adapter);
        // Stop Loop Of Refresh.
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    // Make Menu To Allow User Refresh Menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If You Press On The Same ID.
        if (item.getItemId() == R.id.nav_refresh)
            loadMenu();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_log_out) {
            // Delete User Phone & Password was Saved.
            Paper.book().destroy();

            // GoTo SignIn.
            Intent signIn = new Intent(Home.this, SignIn.class);
            // Make All Fields Empty.
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }

        else if(id == R.id.nav_chng_pwd){
            
            showChangePwdDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePwdDialog() {
        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("Change Password");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Please Fill Full Information");

        // Make LayoutInflater To Adapt This Alert.
        LayoutInflater inflater = this.getLayoutInflater();

        // Make View Use Layout.
        View ChangePwd = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText edtPwd = (MaterialEditText) ChangePwd.findViewById(R.id.edt_password);

        final MaterialEditText edtNewPwd = (MaterialEditText) ChangePwd.findViewById(R.id.edt_new_password);

        final MaterialEditText edtRepeatNewPwd = (MaterialEditText) ChangePwd.findViewById(R.id.edt_repeat_new_password);


        // Set This View.
        alertDialog.setView(ChangePwd);
        // Add Icon To Alert Dialog.
        alertDialog.setIcon(R.drawable.ic_shopping_black);

        // Make Buttons For Alert Dialog [CHANGE , CANCEL].
        // When Press On YES.
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Change Pwd.

                // android.app.AlertDialog For Using SpotsDialog.
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                // Check If Old Pwd Is Right.
                if(edtPwd.getText().toString().equals(Common.currentUser.getPassword())){

                    // Check New Pwd & Repeat Pwd Are The Same Or Not.
                    if(edtNewPwd.getText().toString().equals(edtRepeatNewPwd.getText().toString())){

                        // New Hash Map To Take Key & Val.
                        Map<String, Object> pwdUpdate = new HashMap<>();

                        // Put Into HashMap Take String Of The Same Letters From DataBase.
                        // To Update Key & Value Into Firebase DataBase.
                        pwdUpdate.put("password", edtNewPwd.getText().toString());

                        // Make Update.
                        // Init FireBase.
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        // Reference Or Name Of Our DataBase [Root], In This Case Called [User].
                        DatabaseReference user = database.getReference("User");

                        user.child(Common.currentUser.getPhone()).updateChildren(pwdUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, "Password was updated, Please Sign out.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New password doesn't match !!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Wrong old password !!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // When Press On NO.
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
