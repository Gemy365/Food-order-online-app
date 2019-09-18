package com.example.android.eatitserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatitserver.Commons.Commons;

public class MainActivity extends AppCompatActivity {

    Button BtnSignIn;
    TextView TxtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnSignIn = (Button) findViewById(R.id.btnSignIn);
        TxtSlogan = (TextView) findViewById(R.id.txtSlogan);

        // To Change Type Of Font From Normal To This Font[Nabila.ttf]
        // You Need To Create Folder Called [assets] , make Folder Called fonts [Not font],
        // And Put The File.ttf Inside It.
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        TxtSlogan.setTypeface(typeface);

        // Click On SignIn Button.
        BtnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Check For InterNet. getBaseContext() To Ref Of SignIn,
                // If We Use [this] We Will Ref OnClickListener() Not SignIn And We Need Context Not Method.
                if (Commons.isConnectedToInternet(getBaseContext())) {
                    // GoTo SignIn.java Activity.
                    Intent signIn = new Intent(MainActivity.this, SignIn.class);
                    startActivity(signIn);
                } else {
                    Toast.makeText(MainActivity.this, "Please check your connection..!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
