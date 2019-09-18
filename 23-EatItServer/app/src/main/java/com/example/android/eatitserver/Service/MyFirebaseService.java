package com.example.android.eatitserver.Service;


import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String  tokenRefreshed = FirebaseInstanceId.getInstance().getToken();

        if(Commons.currentUser != null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference tokens = database.getReference("Token");

        // True Cause This Token Send To Client From Server.
        Token token = new Token(tokenRefreshed, true);

        tokens.child(Commons.currentUser.getPhone()).setValue(token);
    }
}
