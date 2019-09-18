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
    // Called This File From Folder assets/databases [If There's No Folder assets/databases Just Make On].
    private final static String DB_NAME = "EatItDB.db";
    // DataBase Version.
    private final static int DB_VER = 1;
    // Constructor.
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    // Method To Return Data As A List Of Order.java
    public List<Order> getCarts(){

        // Init DataBase To Be Readable.
        SQLiteDatabase db = getReadableDatabase();

        // Init DataBase.
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Array Of Strings To Store Data As Into EatItDB.db [Have The Same Order Of Strings].
        String sqSelect[] = {"ProductId", "ProductName", "Quantity", "Price", "Discount"};

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
                result.add(new Order(cursor.getString(cursor.getColumnIndex("ProductId")),
                        cursor.getString(cursor.getColumnIndex("ProductName")),
                        cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount"))
                        ));
                // Still Do It While cursor move To Next Item.
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
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount) VALUES('%s', '%s', '%s', '%s', '%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
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

    // For Add Favorites Food.
    public void addToFavorites(String foodId)
    {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES(%s);", foodId);
        database.execSQL(query);
    }

    // For Remove Favorites Food.
    public void removeFromFavorites(String foodId)
    {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId = '%s';", foodId);
        database.execSQL(query);
    }

    // Check This Food Is Favorites Or Not.
    public boolean isFavorites(String foodId)
    {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId = '%s';", foodId);
        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
