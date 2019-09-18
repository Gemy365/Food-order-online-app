package com.example.android.eatit.Model;

// User Class To Store The Info Of Users [Name, Password].
public class User {
    private String Name;
    private String Password;
    private String Phone;

    // Using [Alt + Insert] To Open Generate To Choose Methods.
    public User() {
    }

    public User(String name, String password, String phone) {
        Name = name;
        Password = password;
        Phone = phone;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
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
