package com.example.android.eatit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;
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
    // MaterialEditText & BtnView & TxtView To Get IDs.
    MaterialEditText edtPhone, edtPassword;
    Button btnSignIn;

    TextView txtForgotPwd;

    CheckBox checkBox;

    FirebaseDatabase database;

    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText) findViewById(R.id.editPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.editPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);

        checkBox = (CheckBox) findViewById(R.id.checkbox_remember);

        // Init Paper To Save key-value, To Save User Name & Password To Remember User.
        Paper.init(this);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called [User].
        table_user = database.getReference("User");

        // Click On txtForgotPwd Text.
        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check For InterNet. getBaseContext() To Ref Of SignIn,
                // If We Use [this] We Will Ref OnClickListener() Not SignIn And We Need Context Not Method.
                if (Common.isConnectedToInternet(getBaseContext()))
                    // Call showForgotPwd Method.
                    showForgotPwd();
                else
                    Toast.makeText(SignIn.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
            }
        });

        // Click On Sign In Button.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check For InterNet. getBaseContext() To Ref Of SignIn,
                // If We Use [this] We Will Ref OnClickListener() Not SignIn And We Need Context Not Method.
                if (Common.isConnectedToInternet(getBaseContext())) {
                    // Save User & Password.
                    // If CheckBox Is Checked.
                    if (checkBox.isChecked()) {
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

                    // Get Values Of User From DataBase.
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Check By Phone Number If User Exist In Data Base Or Not.
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                // Hide The Message.
                                mDialog.dismiss();

                                // Store Children Value Of This Phone Number In User Class.
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

    // Call Method To Make ForgotPwd.
    private void showForgotPwd() {
        // AlertDialog Work With CardView.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set Title.
        builder.setTitle("Forgot Password");

        // Set Message.
        builder.setMessage("Enter your secure code");

        // Make LayoutInflater To Adapt This Alert.
        LayoutInflater inflater = this.getLayoutInflater();

        // Make View Use Layout.
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        // Set This View.
        builder.setView(forgot_view);

        // Set Icon.
        builder.setIcon(R.drawable.ic_security_black_24dp);

        // Get Views By ID.
        final MaterialEditText edtPhone = (MaterialEditText) forgot_view.findViewById(R.id.editPhone_forgotPwd);
        final MaterialEditText edtSecure = (MaterialEditText) forgot_view.findViewById(R.id.editSecureCode_forgotPwd);

        // Click PositiveButton.
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                // Get Ref User From DataBase, Make It's Value Listen To This Change..
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check If Two Fields Not Empty, To Avoid Crashing App.
                        if (!edtPhone.getText().toString().isEmpty() &&
                                !edtSecure.getText().toString().isEmpty()) {

                            // Check If Phone That User Typed It Exists In DataBase Or Not.
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                // Get Value Of Phone From DataBase That User Typed It & Put It Into User Class.
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                                // Check If Secure Code Right.
                                if (user.getSecureCode().equals(edtSecure.getText().toString()))
                                    Toast.makeText(SignIn.this, "Your password :" + user.getPassword(), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(SignIn.this, "Wrong secure code..!!", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(SignIn.this, "Without your number phone, We can't help you", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(SignIn.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();

                            // Call showForgotPwd Method.
                            showForgotPwd();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        // When Press On NO.
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close Alert Dialog.
                dialogInterface.dismiss();
            }
        });
        // After Init All Of This, Show The Alert Dialog.
        builder.show();


    }
}
