package com.example.android.eatit.Common;

import com.example.android.eatit.Model.User;

public class Common {
    // Store The Current User.
    public static User currentUser;

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "placed";

        else if(status.equals("1"))
            return "On my way";

        else
            return "Shipped";
    }
}
