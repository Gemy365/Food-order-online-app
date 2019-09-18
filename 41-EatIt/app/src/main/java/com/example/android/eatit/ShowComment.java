package com.example.android.eatit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.Rating;
import com.example.android.eatit.ViewHolder.MenuViewHolder;
import com.example.android.eatit.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    // For Data Base.
    FirebaseDatabase database;

    // To Get Add Rate Of Food.
    DatabaseReference ratingTbl;

    // For Refresh Menu.
    SwipeRefreshLayout swipeRefreshLayout;

    // Store FoodId.
    String foodId = "";

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerRating;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;
    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;


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

        setContentView(R.layout.activity_show_comment);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Category For Rating.
        ratingTbl = database.getReference("Rating");

        // Load Rating.
        // To Get Image One By One.
        recyclerRating = (RecyclerView) findViewById(R.id.recycler_show_comment);

        // Init layoutManager As A ListView.
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recyclerRating As A ListView.
        recyclerRating.setLayoutManager(layoutManager);

        // Get View By ID For Refresh View When Get Menu Down By Your Finger To Refresh.
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_comment);

        // Set Colors For Refresh If The Refresh Need Long Time.
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // When User Refresh The Page Of Comments.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get FoodId From Intent.
                if (getIntent() != null)
                    // Store ID Into foodId.
                    foodId = getIntent().getStringExtra("FoodId");
                // It's True.
                if (!foodId.isEmpty()) {
                    // Check If InterNet Available.
                    if (Common.isConnectedToInternet(ShowComment.this))
                        // Call This Method By Sending ID.
                        loadComment(foodId);
                    else
                        // If Not Available.
                        Toast.makeText(ShowComment.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // For Default Of First Time.
        // Get FoodId From Intent.
        if (getIntent() != null)
            // Store ID Into foodId.
            foodId = getIntent().getStringExtra("FoodId");
        // It's True.
        if (!foodId.isEmpty()) {
            // Check If InterNet Available.
            if (Common.isConnectedToInternet(ShowComment.this))
                // Call This Method By Sending ID.
                loadComment(foodId);
            else
                // If Not Available.
                Toast.makeText(ShowComment.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadComment(String foodId) {
        // Get All Children Have foodId As The Same foodId.
        Query query = ratingTbl.orderByChild("foodId").equalTo(foodId);

        // Convert From FirebaseUI 1.2.0 To FirebaseUI 3.2.2.
        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query, Rating.class).build();

        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
            @NonNull
            @Override
            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_comment_layout, parent, false);
                return new ShowCommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                holder.rateBar.setRating(Float.parseFloat(model.getRateValue()));
                holder.txtComment.setText(model.getComment());
                holder.txtUserPhone.setText(model.getUserPhone());

            }
        };
        // Start Display Item For Newest FirebaseUI.
        adapter.startListening();
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerRating.setAdapter(adapter);
        // Stop Loop Of Refresh.
        swipeRefreshLayout.setRefreshing(false);
    }
}


