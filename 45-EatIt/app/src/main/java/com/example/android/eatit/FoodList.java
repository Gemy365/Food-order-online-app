package com.example.android.eatit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Interface.ItemClickListener;
import com.example.android.eatit.Model.Food;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {

    // For Data Base.
    FirebaseDatabase database;
    // To Get Images Of Food.
    DatabaseReference foodList;

    // To Store menuId From FireBase DataBase.. [Check menuId To Know It].
    String categoryId = "";

    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    // FireBase Adapter For Searching.
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;

    // Array List For Suggestions.
    List<String> suggestList = new ArrayList<>();

    // For SearchBar.
    MaterialSearchBar materialSearchBar;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerFood;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    // Store Fav Food Into DataBase.
    Database DBForFav;

    // For Refresh food.
    SwipeRefreshLayout swipeRefreshLayout;

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

        setContentView(R.layout.activity_food_list);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Food For Images.
        foodList = database.getReference("Food");

        // DB For Fav Food.
        DBForFav = new Database(this);

        // Get View By ID For Refresh View When Get Menu Down By Your Finger To Refresh.
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

        // Set Colors For Refresh If The Refresh Need Long Time.
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get CategoryId From Intent, It's True When You Click On Image InSide Menu.
                if (getIntent() != null)
                    // Store ID Into categoryId.
                    categoryId = getIntent().getStringExtra("CategoryId");

                // It's True.
                if (!categoryId.isEmpty()) {
                    // Check If InterNet Available.
                    if (Common.isConnectedToInternet(FoodList.this))
                        // Call This Method By Sending ID.
                        loadListFood(categoryId);
                    else
                        // If Not Available.
                        Toast.makeText(FoodList.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        // Get CategoryId From Intent, It's True When You Click On Image InSide Menu.
        if (getIntent() != null)
            // Store ID Into categoryId.
            categoryId = getIntent().getStringExtra("CategoryId");

        // It's True.
        if (!categoryId.isEmpty()) {
            // Check If InterNet Available.
            if (Common.isConnectedToInternet(FoodList.this))
                // Call This Method By Sending ID.
                loadListFood(categoryId);
            else
                // If Not Available.
                Toast.makeText(FoodList.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
        }


        // Get Search Bar By ID.
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search on your favourite food");

        // Call loadSuggest Method To Load Suggest From Firebase [Food's Name].
        loadSuggest();

        // Show Menu Contains Suggestions For Searching [Food's Name].
        materialSearchBar.setLastSuggestions(suggestList);

        // Set The Height [API 21].
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
                for (String search : suggestList) {
                    // If Menu Of Suggestions Contains Same Chars Of Searching Bar [Food's Name].
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
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

        // When User Choose From Suggestion Menu Or Not.
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // When Search Bar Is Close, Restore To Original Adapter.
                if (!enabled)
                    recyclerFood.setAdapter(adapter);
            }

            // When User Choose Food From Suggestion Menu.
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
        // Create Query By Name For Search.
        Query searchName = foodList.orderByChild("name").equalTo(text.toString());

        // Convert From FirebaseUI 1.2.0 To FirebaseUI 3.1.2.
        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchName, Food.class).build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                // To Set Name Of Food Image.
                // model parameter Of Food Own Of This Method.
                // [model.getName() = Key] To Get Value Of Name From DataBase.
                viewHolder.txtFoodName.setText(model.getName());
                // Set Price Of Food.
                viewHolder.txtFoodMoney.setText(String.format(" %s ج.م ", model.getPrice().toString()));
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
        // Start Display Item For Newest FirebaseUI.
        searchAdapter.startListening();
        // Refresh Data If Has Changed On It.
        searchAdapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerFood.setAdapter(searchAdapter);
    }

    // When Call loadSuggest Method.
    private void loadSuggest() {
        // Compare By menuId Of Food.
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // For Each To Get All Children.
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
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

    // foodList.orderByChild("menuId").equalTo(categoryId) Means,
    // Get All Images Have The Same menuId And Make It In The New List View And Adapt Them.
    private void loadListFood(String categoryId) {
        // Create Query By Name For Search.
        Query foodId = foodList.orderByChild("menuId").equalTo(categoryId);

        // Convert From FirebaseUI 1.2.0 To FirebaseUI 3.1.2.
        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodId, Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                // To Set Name Of Food Image Every Time.
                // model parameter Of Food Own Of This Method.
                // [model.getName() = Key] To Get Value Of Name From DataBase.
                viewHolder.txtFoodName.setText(model.getName());
                // Set Price Of Food.
                viewHolder.txtFoodMoney.setText(String.format(" %s ج.م ", model.getPrice().toString()));
                // To Load Food Image From Fire Base And Set It Into imgFoodView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgFoodView);

                // Quick Cart Btn.
                viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To Check If Food In Cart List Or Not, Cause If It Exists..Just Increase Quantity.
                        boolean isFoodExists = new Database(getBaseContext()).checkFoodExists(Common.currentUser.getPhone(), adapter.getRef(position).getKey());

                        // If This Food Not In Cart List [Means This Is First Time That User Buy It].
                        if(!isFoodExists) {
                            // Send This Info To Method Of addToCart Into Database.java.
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));
                        } else{
                            // Increase Quantity On The Old Order OF The Same Order.
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());
                        }
                        Toast.makeText(FoodList.this, "Add To Cart", Toast.LENGTH_SHORT).show();
                    }
                });

                // // Check If It Fave Food.
                if (DBForFav.isFavorites(adapter.getRef(position).getKey()))
                    // Set Img To Be Fav.
                    viewHolder.imgFav.setImageResource(R.drawable.ic_favorite_black_24dp);

                // When Click On Fav Img To Change It To Be Fav Or Not.
                viewHolder.imgFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Check If It Not Fav Food.
                        if (!DBForFav.isFavorites(adapter.getRef(position).getKey())) {
                            // Call addToFavorites Method.
                            DBForFav.addToFavorites(adapter.getRef(position).getKey());
                            // Change Img To Be Fav.
                            viewHolder.imgFav.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "" + model.getName() + " was added to Favorites",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Call removeFromFavorites Method.
                            DBForFav.removeFromFavorites(adapter.getRef(position).getKey());
                            // Change Img To Be Not Fav.
                            viewHolder.imgFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "" + model.getName() + " was removed from Favorites",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

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
        // Start Display Item For Newest FirebaseUI.
        adapter.startListening();
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerFood.setAdapter(adapter);
        // Stop Loop Of Refresh.
        swipeRefreshLayout.setRefreshing(false);
    }
}