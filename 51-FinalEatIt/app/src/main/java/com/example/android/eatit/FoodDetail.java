package com.example.android.eatit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Database.Database;
import com.example.android.eatit.Model.Food;
import com.example.android.eatit.Model.Order;
import com.example.android.eatit.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {
    // Init Views.
    TextView FoodNameDetail, FoodPrice, FoodDescription;
    ImageView FoodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton elegantNumberButton;
    CounterFab btnCart, btnRating;
    RatingBar ratingBar;

    // Store FoodId.
    String foodId = "";

    Food currentFood;


    // For Data Base.
    FirebaseDatabase database;
    // To Get Images Of Food.
    DatabaseReference food;

    // To Get Add Rate Of Food.
    DatabaseReference ratingTbl;

    FButton btnShowComment;

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

        setContentView(R.layout.activity_food_detail);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Food For Images.
        food = database.getReference("Food");
        // Reference Or Name Of Our DataBase [Root], In This Case Called Rating For Rating Food.
        ratingTbl = database.getReference("Rating");

        // Get Views By IDs.
        elegantNumberButton = (ElegantNumberButton) findViewById(R.id.number_btn);

        btnCart = (CounterFab) findViewById(R.id.btn_cart);

        FoodDescription = (TextView) findViewById(R.id.food_description);
        FoodNameDetail = (TextView) findViewById(R.id.food_name_Detail);
        FoodPrice = (TextView) findViewById(R.id.food_price);
        FoodImage = (ImageView) findViewById(R.id.img_food);
        btnRating = (CounterFab) findViewById(R.id.btn_rate);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);


        // Use For Scroll Up To Hide Image & Down To Appear It.
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        btnShowComment = (FButton) findViewById(R.id.btn_show_comment);

        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSowComment = new Intent(FoodDetail.this, ShowComment.class);
                // Cause FoodId is Key, So We Just Get Key Of This Item.
                // putExtra[Key, Value].
                // Send FoodId To FoodDetail Activity.
                intentSowComment.putExtra("FoodId", foodId);
                startActivity(intentSowComment);
            }
        });

        // When Click On btnRating
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call showRatingDialog Method.
                showRatingDialog();
            }
        });

        // When Click On BtnCart.
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send This Info To Method Of addToCart Into Database.java.
                new Database(getBaseContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        foodId,
                        currentFood.getName(),
                        elegantNumberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                ));
                Toast.makeText(FoodDetail.this, "جاهز لطلب الأوردر", Toast.LENGTH_SHORT).show();
            }
        });
        // Set Number Of Orders On This Btn.
        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));


        // Get FoodId From Intent.
        if (getIntent() != null)
            // Store ID Into foodId.
            foodId = getIntent().getStringExtra("FoodId");
        // It's True.
        if (!foodId.isEmpty())
            // Check If InterNet Available
            if (Common.isConnectedToInternet(this)) {
                // Call This Method By Sending ID.
                getDetailFood(foodId);

                // Call This Method By Sending ID For Rating Bar.
                getRatingFood(foodId);

            } else
                // If Not Available.
                Toast.makeText(FoodDetail.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
    }

    // Called For Getting Rating Bar Of This Food [Not Rate Of User, But Sum Rates Of Users To This Food].
    private void getRatingFood(String foodId) {
        // Get Food Rating From FireBase By food Id.
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop For To Get All Children  Have Same foodId To Rate It.
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get All Value From Rating Class.
                    Rating item = postSnapshot.getValue(Rating.class);

                    // Sum Of Rating User.
                    sum += Integer.parseInt(item.getRateValue());

                    // How Many Times Rating.
                    count++;
                }
                if (count != 0) {
                    // Make average To Be Max 5 Or Less Than.
                    float average = sum / count;

                    // Set Rating Bar.
                    ratingBar.setRating(average);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // When Call showRatingDialog Method, Set LayOut & Options Of This App Rating Dialog.
    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.red)
                .setDescriptionTextColor(R.color.red)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.white)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.red)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();


    }

    @Override
    public void onNegativeButtonClicked() {

    }

    // When Click On Positive Button.
    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        // Call Rating Constructor & Send info To It.
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);

        // Fix To Make User Rake Multiple Times.
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "شكرآ لدعمك", Toast.LENGTH_SHORT).show();
                    }
                });
/*
        ratingTbl.child(String.valueOf(System.currentTimeMillis())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Update New Value.
                    ratingTbl.child(String.valueOf(System.currentTimeMillis())).setValue(rating);

                Toast.makeText(FoodDetail.this, "Thank you for submit rating", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
    }

    @Override
    public void onNeutralButtonClicked() {

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

