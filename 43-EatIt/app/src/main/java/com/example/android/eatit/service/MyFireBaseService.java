package com.example.android.eatit.service;

import com.example.android.eatit.Common.Common;
import com.example.android.eatit.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// This For Unique Token For Every One.
public class MyFireBaseService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String  tokenRefreshed = FirebaseInstanceId.getInstance().getToken();

        if(Common.currentUser != null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference tokens = database.getReference("Token");

        // False Cause This Token Send From Client To Server.
        Token token = new Token(tokenRefreshed, false);

        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
