package com.example.android.eatit.Model;

public class User {
    private String Name;
    private String Password;

    // Using [Alt + Insert] To Open Generate To Choose Methods.
    public User() {
    }

    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
