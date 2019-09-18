package com.example.android.eatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    MaterialEditText edtPhone, edtPassword;
    Button btnSignIn;

    com.rey.material.widget.CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText) findViewById(R.id.editPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.editPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        checkBox = (com.rey.material.widget.CheckBox) findViewById(R.id.checkbox_remember);

        // Init Paper To Save key-value.
        Paper.init(this);

        // Init FireBase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called User.
        final DatabaseReference table_user = database.getReference("User");

        // Click On Sign In Button.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check For InterNet. getBaseContext() To Ref Of SignIn,
                // If We Use [this] We Will Ref OnClickListener() Not SignIn And We Need Context Not Method.
                if (Common.isConnectedToInternet(getBaseContext())) {
                    // Save User & Password.
                    // If CheckBos Checked.
                    if(checkBox.isChecked()){
                        // [.write Take Key & Value] Use Key Of User, And Value Of Number Phone That User Typed It.
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());

                        // [.write Take Key & Value]  Use Key Of Password, And Value Of Password That User Typed It.
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    // Show Message In Device.
                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    // Make Your Own Message.
                    mDialog.setMessage("Please wait...");
                    // Show The Message.
                    mDialog.show();

                    // Get Values From DataBase.
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Check By Phone Number If User Exist In Data Base Or Not.
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                // Hide The Message.
                                mDialog.dismiss();

                                // Store Children of This Phone Number In User Class.
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                                // Because Phone Not Value In FireBase [It's Key As A Root ], Need To Set phone.
                                user.setPhone(edtPhone.getText().toString());
                                // Check If The Password Is Right Or Wrong.
                                if ((edtPassword.getText().toString()).equals(user.getPassword())) {
                                    // Goto Home.java.
                                    Intent HomeIntent = new Intent(SignIn.this, Home.class);

                                    // Store Values In The Current User.
                                    Common.currentUser = user;

                                    // Start The Activity
                                    startActivity(HomeIntent);

                                    // Out From Fire Base.
                                    finish();
                                } else {
                                    Toast.makeText(SignIn.this, "Wrong Password..!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Hide The Message.
                                mDialog.dismiss();

                                Toast.makeText(SignIn.this, "Please Sign Up First",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignIn.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
