package com.example.android.eatit;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    }
}
