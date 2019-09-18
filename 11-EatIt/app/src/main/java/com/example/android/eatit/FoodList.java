package com.example.android.eatit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Food;
import com.example.android.eatit.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    // For Data Base.
    FirebaseDatabase database;
    // To Get Images Of Food.
    DatabaseReference foodList;

    // To Store MenuId From FireBase DataBase.. [Check MenuId To Know It]
    String categoryId = "";

    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    // FireBase Adapter For Searching.
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;

    // Array List For Suggestions.
    List<String> suggestList = new ArrayList<>();

    MaterialSearchBar materialSearchBar;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerFood;
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
        recyclerFood = (RecyclerView) findViewById(R.id.recycler_food);
        // Make All Has The Same Size.
        recyclerFood.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_food As A ListView.
        recyclerFood.setLayoutManager(layoutManager);

        // Get CategoryId From Intent, It's True When You Click On Image On Menu.
        if (getIntent() != null)
            // Store ID Into categoryId.
            categoryId = getIntent().getStringExtra("CategoryId");

        // It's True.
        if (!categoryId.isEmpty())
            // Call This Method By Sending ID.
            loadListFood(categoryId);

        // Get Search Bar By ID.
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search on your favourite food");

        // Call loadSuggest Method To Load Suggest From Firebase.
        loadSuggest();

        // Show Menu Contains Suggestions For Searching [Food's Name].
        materialSearchBar.setLastSuggestions(suggestList);

        // Set The Height.
        materialSearchBar.setCardViewElevation(10);

        // When User Type Into Search Bar.
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            // When There's Change On Text Into Search Bar,
            // Make List Of String To Store Words One By One.
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<String>();
                // For Each To Move On All suggestList.
                for(String search:suggestList){
                    // If Menu Of Suggestions Contains Same Chars Of Searching Bar [Food's Name].
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        // Add This word Into Var suggest.
                        suggest.add(search);
                }
                // After End Of This, Make This Word Only [Food's Name] Into Menu Of Suggestions.
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // When User Choose From Suggestion Menu Or Not
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // When Search Bar Is Close, Restore To Original Adapter.
                if(!enabled)
                    recyclerFood.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // When Search Finish, Show The Result Of Search Adapter.
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    // When Call startSearch Method.
    private void startSearch(CharSequence text) {
        // Init Adapter For Searching.
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                // text Come From User's Search, & Compare By Name Of Food.
                foodList.orderByChild("Name").equalTo(text.toString()))
        {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                // To Set Name Of Food Image.
                // model parameter Of Food Own Of This Method.
                // [model.getName() = Key] To Get Value Of Name From DataBase.
                viewHolder.txtFoodName.setText(model.getName());
                // To Load Food Image From Fire Base And Set It Into imgFoodView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgFoodView);

                // When Click On Image.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Goto FoodDetail Activity.
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        // Cause FoodId is Key, So We Just Get Key Of This Item.
                        // putExtra[Key, Value].
                        // Send FoodId To FoodDetail Activity.
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        // Start Activity.
                        startActivity(foodDetail);
                    }
                });
            }
        };
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerFood.setAdapter(searchAdapter);
    }

    // When Call loadSuggest Method.
    private void loadSuggest() {
        // Compare By MenuId Of Food.
        foodList.orderByChild("MenuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // For Each To Get All Children.
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            // All The Time Get Name Of Food And Put It Into Menu Of Suggestion.
                            Food item = postSnapshot.getValue(Food.class);
                            // Add Name Of Food To Suggest List.
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // foodList.orderByChild("MenuId").equalTo(categoryId) Means,
    // Get All Images Have The Same MenuId And Make It In The New List View And Adapt Them.
    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item,
                FoodViewHolder.class, foodList.orderByChild("menuId").equalTo(categoryId)) {

            /**
             *
             * @param viewHolder From FoodViewHolder Own Class.
             * @param model From Food Own Class.
             * @param position To Get Item By Position.
             */
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                // To Set Name Of Food Image Every Time.
                // model parameter Of Food Own Of This Method.
                // [model.getName() = Key] To Get Value Of Name From DataBase.
                viewHolder.txtFoodName.setText(model.getName());
                // To Load Food Image From Fire Base And Set It Into imgFoodView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgFoodView);
                // Store Values Of model [getName(), getImage()] Into local.
                final Food local = model;

                // When Click On Image.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Goto FoodDetail Activity.
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        // Cause FoodId is Key, So We Just Get Key Of This Item.
                        // putExtra[Key, Value].
                        // Send FoodId To FoodDetail Activity.
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        // Start Activity.
                        startActivity(foodDetail);
                    }
                });
            }
        };
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerFood.setAdapter(adapter);
    }
}