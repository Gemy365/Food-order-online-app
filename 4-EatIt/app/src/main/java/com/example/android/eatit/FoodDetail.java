package com.example.android.eatit;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.eatit.Model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {
    TextView FoodNameDetail, FoodPrice, FoodDescription;
    ImageView FoodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton elegantNumberButton;
    FloatingActionButton btnCart;

    String foodId = "";

    // For Data Base.
    FirebaseDatabase database;
    // To Get Images Of Food.
    DatabaseReference food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Food For Images.
        food = database.getReference("Food");

        // Init View.
        elegantNumberButton = (ElegantNumberButton) findViewById(R.id.number_btn);

        btnCart = (FloatingActionButton) findViewById(R.id.btn_cart);

        FoodDescription = (TextView) findViewById(R.id.food_description);
        FoodNameDetail = (TextView) findViewById(R.id.food_name_Detail);
        FoodPrice = (TextView) findViewById(R.id.food_price);
        FoodImage = (ImageView) findViewById(R.id.img_food);

        // Use For Scroll Up To Hide Image & Down To Appear It.
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // Get FoodId From Intent.
        if (getIntent() != null)
            // Store ID Into categoryId.
            foodId = getIntent().getStringExtra("FoodId");
        // It's True.
        if (!foodId.isEmpty())
            // Call This Method By Sending ID.
            getDetailFood(foodId);

    }

    // foodList.orderByChild("MenuId").equalTo(categoryId) Means,
    // Get All Images Have The Same MenuId And Make It In The New List View And Adapt Them.
    private void getDetailFood(String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food foods = dataSnapshot.getValue(Food.class);

                // Get image From DataBase & Set Image Into FoodImage.
                Picasso.with(getBaseContext()).load(foods.getImage()).into(FoodImage);

                // Set Name Of Food On Image.
                collapsingToolbarLayout.setTitle(foods.getName());

                // Set price Of Food.
                FoodPrice.setText(foods.getPrice());

                // Set Name Of Food.
                FoodNameDetail.setText(foods.getName());

                // Set Description Of Food.
                FoodDescription.setText(foods.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

