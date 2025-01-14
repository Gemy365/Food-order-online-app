package com.example.android.eatit.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    // Store Order Of User.
    private String status;
    private String comment;
    // For Correct Place Name Of Order.
 // private String latLng;
    // List Of Food Order.
    private List<Order> foods;

    public Request() {
    }


    public Request(String phone, String name, String address, String comment, String total, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        // Default is 0 , 0: Placed , 1: Shipping , 2: Shipped.
        this.status = "0";
        this.comment = comment;
 //     this.latLng = latLng;
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public String getLatLng() {
//        return latLng;
//    }
//
//    public void setLatLng(String latLng) {
//        this.latLng = latLng;
//    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
