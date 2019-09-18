package com.example.android.eatitserver.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String clock;
    private String total;
    // status Order Of User.
    private String status;
    private String address;
    // For Correct Place Name Of Order.
    // private String latLng;
    // List Of Food Order.
    private List<Order> foods;

    public Request() {
    }

    public Request(String phone, String name, String clock,  String address, String total, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.clock = clock;
        this.total = total;
        // Default is 0 , 0: Placed , 1: Shipping , 2: Shipped.
        this.status = "0";
        this.address = address;
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

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    //    public String getLatLng() {
//        return latLng;
//    }
//
//    public void setLatLng(String latLng) {
//        this.latLng = latLng;
//    }



}
