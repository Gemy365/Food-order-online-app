package com.example.android.eatit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.User;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    // Constant For AccountKit.
    private static final int REQUEST_CODE = 7171;
    // BtnView & TxtView To Get IDs.
    Button BtnContinue;
    TextView TxtSlogan;

    FirebaseDatabase database;
    DatabaseReference users;

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

        // Init AccountKit Before setContentView.
        AccountKit.initialize(this);

        setContentView(R.layout.activity_main);

        // Init Firebase.
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        // For developers.facebook.com To Allow Me To Sign In User By Number Phone
        // Get Hash Key Of This App
        // Hash Key Used In developers.facebook.com / Settings / Basic.
        printKeyHash();

        // Get IDs.
        BtnContinue = (Button) findViewById(R.id.btn_continue);

        TxtSlogan = (TextView) findViewById(R.id.txtSlogan);

        // To Change Type Of Font From Normal To This Font[Nabila.ttf]
        // You Need To Create Folder Called [assets] , make Folder Called [fonts] Not [font],
        // Download File.TTF & Put The File.ttf Inside It.
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        // setTypeface For Words Get It By It's ID.
        TxtSlogan.setTypeface(typeface);

        // Click On BtnContinue Button.
        BtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send SMS To User Include Secret Code When Type His Phone To Secure App.
                startLoginSystem();
            }
        });

        // Check If This Is Second Time For User, So Login Auto.
        if (AccountKit.getCurrentAccessToken() != null) {
            // Create Dialog.
            final AlertDialog waitDialog = new SpotsDialog(this);
            waitDialog.show();
            waitDialog.setMessage("Pleate wait");
            waitDialog.setCancelable(false);

            // Get Current Numper Phone.
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    // Get This Phone From Firebase [User] To Login Auto.
                    users.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User localUser = dataSnapshot.getValue(User.class);
                                    // Copy Code From LoginActivity.
                                    // Goto Home.java.
                                    Intent HomeIntent = new Intent(MainActivity.this, Home.class);

                                    // Store Values In The Current User.
                                    Common.currentUser = localUser;

                                    // Start The Activity
                                    startActivity(HomeIntent);

                                    // Dismiss Dialog.
                                    waitDialog.dismiss();

                                    // Out From Fire Base.
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }
    }

    // Method For Login by Phone Number [Feature From FaceBook Developers].
    private void startLoginSystem() {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    // Called From startLoginSystem() Method.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // It's True With Any Number Of REQUEST_CODE.
        if (requestCode == REQUEST_CODE) {
            // Make New Token And Store It Into result Var.
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            // Check If result Get Error.
            if (result.getError() != null) {
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            // Check If result wasCancelled.
            else if (result.wasCancelled()) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Check If Can Get New Token For New User & It's Be Not Null.
                if (result.getAccessToken() != null) {

                    // Create Dialog.
                    final AlertDialog waitDialog = new SpotsDialog(this);

                    // Show Dialog.
                    waitDialog.show();

                    waitDialog.setMessage("Please wait");

                    waitDialog.setCancelable(false);

                    // Get New Current Phone Number.
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        // When Success With New Phone Number.
                        @Override
                        public void onSuccess(Account account) {
                            // Store New Phone Number Into userPhone Var.
                            final String userPhone = account.getPhoneNumber().toString();

                            // Search On Key Of User Root Equal This Number Phone.
                            users.orderByKey().equalTo(userPhone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            // If Number Phone Not Exists [Means New Number Phone].
                                            if (!dataSnapshot.child(userPhone).exists()) {
                                                // Send New Info To User Class To Create New User & Login.
                                                User newUser = new User();
                                                // Set This Phone.
                                                newUser.setPhone(userPhone);
                                                // Set No Name For New Phone.
                                                newUser.setName("");

                                                // Add This Phone To FireBase.
                                                users.child(userPhone)
                                                        // Set Value From User Class.
                                                        .setValue(newUser)

                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                // Check If All Things Is Right.
                                                                if (task.isSuccessful())
                                                                    Toast.makeText(MainActivity.this, "User register successful", Toast.LENGTH_SHORT).show();

                                                                // Login.
                                                                users.child(userPhone)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                // Get Info From Firebase Into User Class.
                                                                                User localUser = dataSnapshot.getValue(User.class);
                                                                                // Goto Home.java.
                                                                                Intent HomeIntent = new Intent(MainActivity.this, Home.class);

                                                                                // Store Values In The Current User.
                                                                                Common.currentUser = localUser;

                                                                                // Start The Activity
                                                                                startActivity(HomeIntent);

                                                                                // Dismiss Dialog.
                                                                                waitDialog.dismiss();

                                                                                // Out From Fire Base.
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                            // If User Exists.
                                            else {
                                                // Just Login.
                                                users.child(userPhone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                User localUser = dataSnapshot.getValue(User.class);
                                                                // Goto Home.java.
                                                                Intent HomeIntent = new Intent(MainActivity.this, Home.class);

                                                                // Store Values In The Current User.
                                                                Common.currentUser = localUser;

                                                                // Start The Activity
                                                                startActivity(HomeIntent);

                                                                // Dismiss Dialog.
                                                                waitDialog.dismiss();

                                                                // Out From Fire Base.
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, "" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    // Print Hash Key Of This App Into Console
    // For developers.facebook.com To Allow Me To Sign In User By Number Phone
    // Get Hash Key Of This App
    // Hash Key Used In developers.facebook.com / Settings / Basic..
    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.android.eatit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // When login Method Is Called.
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
                    // Hide The Message.
                    mDialog.dismiss();

                    //Check By Phone Number If User Exist In Data Base Or Not.
                    if (dataSnapshot.child(phone).exists()) {

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
                        }
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

