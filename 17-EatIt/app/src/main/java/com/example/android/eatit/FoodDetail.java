package com.example.android.eatit;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Model.Food;
import com.example.android.eatit.Model.Order;
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
    Button btnCart;

    String foodId = "";

    Food currentFood;


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

        btnCart = (Button) findViewById(R.id.btn_cart);

        FoodDescription = (TextView) findViewById(R.id.food_description);
        FoodNameDetail = (TextView) findViewById(R.id.food_name_Detail);
        FoodPrice = (TextView) findViewById(R.id.food_price);
        FoodImage = (ImageView) findViewById(R.id.img_food);

        // Use For Scroll Up To Hide Image & Down To Appear It.
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // When Click On BtnCart.
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send This Info To Method Of addToCart Into Database.java.
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        elegantNumberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "Add To Cart", Toast.LENGTH_SHORT).show();
            }
        });

        // Get FoodId From Intent.
        if (getIntent() != null)
            // Store ID Into foodId.
            foodId = getIntent().getStringExtra("FoodId");
        // It's True.
        if (!foodId.isEmpty())
            // Check If InterNet Available
            if(Common.isConnectedToInternet(this))
                // Call This Method By Sending ID.
                getDetailFood(foodId);
            else
                // If Not Available.
                Toast.makeText(FoodDetail.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
    }

    // foodList.orderByChild("MenuId").equalTo(categoryId) Means,
    // Get All Images Have The Same MenuId And Make It In The New List View And Adapt Them.
    private void getDetailFood(String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Values From DataBase & Store Them In Food Class To Return Them.
                currentFood = dataSnapshot.getValue(Food.class);

                // Get image From DataBase & Set Image Into FoodImage.
                // [foods.getImage() = Key] To Get Value Of Image From DataBase.
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(FoodImage);

                // Set Name Of Food On Image.
                collapsingToolbarLayout.setTitle(currentFood.getName());

                // Set price Of Food.
                FoodPrice.setText(currentFood.getPrice());

                // Set Name Of Food.
                FoodNameDetail.setText(currentFood.getName());

                // Set Description Of Food.
                FoodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

