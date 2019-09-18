package com.example.android.eatit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Category;
import com.example.android.eatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // For Data Base.
    FirebaseDatabase database;
    // To Get Images
    DatabaseReference category;

    TextView txtFullName;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recycler_menu;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Make Title For This Menu
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Category For Images.
        category = database.getReference("Category");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        txtFullName = (TextView) headerView.findViewById(R.id.text_full_name);
        // Set Name Of User In Menu.
        txtFullName.setText(Common.currentUser.getName());

        // Load Menu.
        // To Get Image One By One.
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        // Make All Has The Same Size.
        recycler_menu.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_menu As A ListView.
        recycler_menu.setLayoutManager(layoutManager);
        // Call This Method To Adapter This List View To Appear Info On Device After Adapt It.
        loadMenu();
    }

    // Method.
    private void loadMenu() {

        // Adapter Take Two Parameters. To Appear The Information On The Device.
        // As  My ListOfCall Program, Was Need To List View And Adapter.
        FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter = new FirebaseRecyclerAdapter<Category,
                MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {

            /**
             *
             * @param viewHolder From MenuViewHolder Own Class.
             * @param model From Category Own Class.
             * @param position To Get Item By Position.
             */
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                // To Set Name Of Image Every Time.
                // model parameter Of Category Own Of This Method.
                viewHolder.txtMenuName.setText(model.getName());
                // To Load Image From Fire Base And Set It Into imageView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                // Store Values Of model [getName(), getImage()] Into ClickItem.
                final Category clickItem = model;

                // When Click On Image.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        // After End All Of This Set Adapter To Appear It On The Device.
        recycler_menu.setAdapter(adapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_log_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
