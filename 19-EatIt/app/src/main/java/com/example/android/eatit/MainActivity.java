package com.example.android.eatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button BtnSignUp, BtnSignIn;
    TextView TxtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnSignUp = (Button) findViewById(R.id.btnSignUp);
        BtnSignIn = (Button) findViewById(R.id.btnSignIn);
        TxtSlogan = (TextView) findViewById(R.id.txtSlogan);

        // To Change Type Of Font From Normal To This Font[Nabila.ttf]
        // You Need To Create Folder Called [assets] , make Folder Called fonts [Not font],
        // And Put The File.ttf Inside It.
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        TxtSlogan.setTypeface(typeface);

        // Init Paper To Save key-value.
        Paper.init(this);

        // Click On SignUp Button.
        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GoTo SignIn.java Activity.
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        // Click On SignIn Button.
        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GoTo SignIn.java Activity.
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);

            }
        });

        // Check If User Save User & Password, So LogIn Without Ask Him Again.
        // [.read Take Key] and Get Value, Get Number Phone Was Stored.
        String phone = Paper.book().read(Common.USER_KEY);

        // [.read Take Key] and Get Value, Get Password Was Stored.
        String password = Paper.book().read(Common.PWD_KEY);

        // Check If There's Values Stored.
        if (phone != null && password != null) {
            // Check If There's Values Not Empty To Surely.
            if (!phone.isEmpty() && !password.isEmpty()) {
                login(phone, password);
            }
        }
    }

    private void login(final String phone, final String password) {
        // Init FireBase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called User.
        final DatabaseReference table_user = database.getReference("User");

        // Check For InterNet. getBaseContext() To Ref Of SignIn,
        // If We Use [this] We Will Ref OnClickListener() Not SignIn And We Need Context Not Method.
        if (Common.isConnectedToInternet(getBaseContext())) {
            // Show Message In Device.
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            // Make Your Own Message.
            mDialog.setMessage("Please wait...");
            // Show The Message.
            mDialog.show();

            // Get Values From DataBase.
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Check By Phone Number If User Exist In Data Base Or Not.
                    if (dataSnapshot.child(phone).exists()) {
                        // Hide The Message.
                        mDialog.dismiss();

                        // Store Children of This Phone Number In User Class.
                        User user = dataSnapshot.child(phone).getValue(User.class);

                        // Because Phone Not Value In FireBase [It's Key As A Root ], Need To Set phone.
                        user.setPhone(phone);
                        // Check If The Password Is Right Or Wrong.
                        if ((password).equals(user.getPassword())) {
                            // Goto Home.java.
                            Intent HomeIntent = new Intent(MainActivity.this, Home.class);

                            // Store Values In The Current User.
                            Common.currentUser = user;

                            // Start The Activity
                            startActivity(HomeIntent);

                            // Out From Fire Base.
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password..!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Hide The Message.
                        mDialog.dismiss();

                        Toast.makeText(MainActivity.this, "Please Sign Up First",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
        }
    }
}

