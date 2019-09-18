package com.example.android.eatit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Food;
import com.example.android.eatit.ViewHolder.FoodViewHolder;
import com.example.android.eatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    // For Data Base.
    FirebaseDatabase database;
    // To Get Images Of Food.
    DatabaseReference foodList;

    // To Store MenuId From FireBase DataBase.. [Check MenuId To Know It]
    String categoryId = "";

    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recycler_food;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Food For Images.
        foodList = database.getReference("Food");

        // Load Menu.
        // To Get Image Of Food One By One.
        recycler_food = (RecyclerView) findViewById(R.id.recycler_food);
        // Make All Has The Same Size.
        recycler_food.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_food As A ListView.
        recycler_food.setLayoutManager(layoutManager);

        // Get Intent Here, It's True When You Click On Image On Menu.
        if (getIntent() != null)
            // Store ID Into categoryId.
            categoryId = getIntent().getStringExtra("CategoryId");

        // It's True.
        if (!categoryId.isEmpty() && categoryId != null)
            // Call This Method By Sending ID.
            loadListFood(categoryId);

    }
    // foodList.orderByChild("MenuId").equalTo(categoryId) Means,
    // Get All Images Have The Same MenuId And Make It In The New List View And Adapt Them.
    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item,
                FoodViewHolder.class, foodList.orderByChild("MenuId").equalTo(categoryId)) {

            /**
             *
             * @param viewHolder From FoodViewHolder Own Class.
             * @param model From Food Own Class.
             * @param position To Get Item By Position.
             */
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                // To Set Name Of Food Image Every Time.
                // model parameter Of Category Own Of This Method.
                viewHolder.txtFoodName.setText(model.getName());
                // To Load Food Image From Fire Base And Set It Into imgFoodView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgFoodView);
                // Store Values Of model [getName(), getImage()] Into local.
                final Food local = model;

                // When Click On Image.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        // After End All Of This Set Adapter To Appear It On The Device.
        recycler_food.setAdapter(adapter);
    }
}