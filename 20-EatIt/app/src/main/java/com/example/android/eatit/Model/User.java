package com.example.android.eatit.Model;

// User Class To Store The Info Of Users [Name, Password].
public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String SecureCode;


    // Using [Alt + Insert] To Open Generate To Choose Methods.
    public User() {
    }

    public User(String name, String password, String secureCode) {
        Name = name;
        Password = password;
        IsStaff = "fales";
        SecureCode = secureCode;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }
}
