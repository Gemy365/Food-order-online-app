package com.example.android.eatit;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = (MaterialEditText) findViewById(R.id.editPhone);
        edtName = (MaterialEditText) findViewById(R.id.editName);
        edtPassword = (MaterialEditText) findViewById(R.id.editPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        // Init FireBase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called User.
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check If InterNet Available.
                if(Common.isConnectedToInternet(getBaseContext())){
                // Show Message In Device.
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                // Make Your Own Message.
                mDialog.setMessage("Please wait...");
                // Show The Message.
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check By Phone Number If User Exist In Data Base Or Not.
                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            // Hide The Message.
                            mDialog.dismiss();

                            Toast.makeText(SignUp.this, "The phone number is registered",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Hide The Message.
                            mDialog.dismiss();

                            // Send This Info For User Class.
                            User user = new User(edtName.getText().toString(),
                                    edtPassword.getText().toString());

                            // Make New Child For DataBase And Set Children Of This New Phone Number.
                            table_user.child(edtPhone.getText().toString()).setValue(user);

                            Toast.makeText(SignUp.this, "Sign Up successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // If Not Available.
                } else {
                    Toast.makeText(SignUp.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
