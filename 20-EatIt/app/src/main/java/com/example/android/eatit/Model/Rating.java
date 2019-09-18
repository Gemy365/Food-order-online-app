package com.example.android.eatit.Model;

public class Rating {
    // Key And Value, Cause One User Have Make One Rate.
    private String UserPhone;
    private String FoodId;
    private String RateValue;
    private String Comment;

    public Rating() {
    }

    public Rating(String userPhone, String foodId, String rateValue, String comment) {
        UserPhone = userPhone;
        FoodId = foodId;
        RateValue = rateValue;
        Comment = comment;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getRateValue() {
        return RateValue;
    }

    public void setRateValue(String rateValue) {
        RateValue = rateValue;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
