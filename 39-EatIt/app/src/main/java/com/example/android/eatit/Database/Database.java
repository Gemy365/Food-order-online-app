package com.example.android.eatit.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.android.eatit.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

// Extends From  SQLiteAssetHelper.
public class Database extends SQLiteAssetHelper{
    // EatItDB.db Was Created By DB [Browser for SQLite Program] To Hold The DataBase.
    // Called This File From Folder assets/databases [If There's No Folder assets/databases Just Make One].
    private final static String DB_NAME = "EatItDB.db";
    // DataBase Version.
    private final static int DB_VER = 2;
    // Constructor.
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    // Method To Return Data As A List Of Order.java.
    public List<Order> getCarts(){

        // Init DataBase To Be Readable.
        SQLiteDatabase db = getReadableDatabase();

        // Init DataBase.
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Array Of Strings To Store Data As Into EatItDB.db [Have The Same Order Of Strings].
        String sqSelect[] = {"ID", "ProductId", "ProductName", "Quantity", "Price", "Discount", "Image"};

        // OrderDetail This Is A Table Of DataBase Into EatItDB.db.
        String sqlTable = "OrderDetail";

        // Set The OrderDetail Table.
        qb.setTables(sqlTable);

        // Cursor To Move On DataBase, qb.query Take Two Params In This Case.
        // Type Of DataBase [SQLiteDatabase To Read It] , Keys Of Table.
        Cursor cursor = qb.query(db, sqSelect, null, null, null, null, null);

        // Make result To Store List Of Order.
        final List<Order> result = new ArrayList<>();
        // If cursor Start To Read DataBase.
        if(cursor.moveToFirst()){
            do {
                // Add New Constructor Of Order To Get String [As We Make Type Of ProductId (Text) In EatItDB.db]
                // When Cursor Move To ProductId And Other Items, And Store Into result Variable.
                result.add(new Order(
                        cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("ProductId")),
                        cursor.getString(cursor.getColumnIndex("ProductName")),
                        cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount")),
                        cursor.getString(cursor.getColumnIndex("Image"))
                        ));
                // Still Do It While cursor move To Next Order.
            }while (cursor.moveToNext());
        }
        // After All Is Done, Return This Info As A List To Order.java
        return result;
    }

    // When User Add Food To His Cart [Make Order].
    public void addToCart(Order order){
        // Init DataBase To Be Readable.
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        // Insert Or Store Values From User Into Table Of DataBase [OrderDetail].
        // Make Sure From Order.java Has The Exactly Same Name Of Items Into OrderDetail.
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount, Image) VALUES('%s', '%s', '%s', '%s', '%s', '%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        // After End Of This, ExecuteSQL By New Data Of This Variable query.
        sqLiteDatabase.execSQL(query);
    }

    // After User Get His Order We Need To Delete The Order Was Delivered.
    public void cleanCart(){
        // Init DataBase To Be Readable.
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        // Make Delete From OrderDetail.
        String query = String.format("DELETE FROM OrderDetail");
        // After End Of This, ExecuteSQL By New Data Of This Variable query.
        sqLiteDatabase.execSQL(query);
    }

    // To Calculate Number Of Orders.
    public int getCountCart(){
        int count = 0;

        // Init DataBase To Be Readable.
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        // Make Count Of All Column From OrderDetail.
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        // Cursor To Move On DataBase.
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        // If cursor Start To Read DataBase.
        if(cursor.moveToFirst()){
            do{
                // Get Int Of First Column.
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    // For Add Favorites Food.
    public void addToFavorites(String foodId, String userPhone)
    {
        // Init DataBase To Be Readable.
        SQLiteDatabase database = getReadableDatabase();
        // Add Foods By Own ID To Database.
        String query = String.format("INSERT INTO Favorites(FoodId, UserPhone) VALUES(%s, %s);", foodId, userPhone);
        // After End Of This, ExecuteSQL By New Data Of This Variable query.
        database.execSQL(query);
    }

    // For Remove Favorites Food.
    public void removeFromFavorites(String foodId, String userPhone)
    {
        // Init DataBase To Be Readable.
        SQLiteDatabase database = getReadableDatabase();
        // Delete Foods By Own ID From Database.
        String query = String.format("DELETE FROM Favorites WHERE FoodId = '%s' and UserPhone = '%s';", foodId, userPhone);
        // After End Of This, ExecuteSQL By New Data Of This Variable query.
        database.execSQL(query);
    }

    // Check This Food Is Favorites Or Not.
    public boolean isFavorites(String foodId, String userPhone)
    {
        // Init DataBase To Be Readable.
        SQLiteDatabase database = getReadableDatabase();
        // Select All Column Where FoodId In Database Equal foodId Of Food.
        String query = String.format("SELECT * FROM Favorites WHERE FoodId = '%s' and UserPhone = '%s';", foodId, userPhone);
        // Cursor To Move On All Values.
        Cursor cursor = database.rawQuery(query, null);

        // If foodId Exist, So Return True, If Not Exist Return False.
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void updateCart(Order order){
        // Init DataBase To Be Readable.
        SQLiteDatabase database = getReadableDatabase();
        // Update Quantity By ID Of Food [Primary Key Of OrderDetail Table Of Database].
        String query = String.format("UPDATE OrderDetail SET Quantity = %s WHERE ID = %d", order.getQuantity(), order.getID());
        // After End Of This, ExecuteSQL By New Data Of This Variable query.
        database.execSQL(query);
    }

}
