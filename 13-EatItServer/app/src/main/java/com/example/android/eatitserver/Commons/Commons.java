package com.example.android.eatitserver.Commons;

import com.example.android.eatitserver.Model.User;

public class Commons {
    public static User currentUser;

    public static final  String UPDATE = "Update";

    public static final  String DELETE = "Delete";

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "placed";

        else if(status.equals("1"))
            return "On my way";

        else
            return "Shipped";
    }
}
