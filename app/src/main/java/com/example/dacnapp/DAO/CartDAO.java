package com.example.dacnapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dacnapp.Helper.dbHelper;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.Model.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class CartDAO {
    dbHelper dbHelper;

    public CartDAO(Context context) {
        dbHelper = new dbHelper(context);
    }

    public ArrayList<Cart> getAllCart(String userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ArrayList<Cart> listCart = new ArrayList<>();
        String query = "SELECT * FROM Cart WHERE userId = ?";
        String[] selectionArgs = {userId};
        Cursor c = database.rawQuery(query, selectionArgs);
        while (c.moveToNext()) {
            Cart temp = new Cart();
            temp.setProductId(c.getString(0));
            temp.setSoLg(c.getInt(2));
            listCart.add(temp);
        }
        return listCart;
    }

    public void addToCart(Product product, String userId) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "userId = ? AND productId = ?";
        String[] selectionArgs = {userId, product.getId()};

        Cursor cursor = db.query("Cart", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            System.out.println(product.getThumb());

            int quantityIndex = cursor.getColumnIndex("SoLg");
            if (quantityIndex >= 0) {
                int quantity = cursor.getInt(quantityIndex) + 1;
                updateSoLg(userId, product.getId(), quantity);
            }
        } else {
            ContentValues values = new ContentValues();
            values.put("productId", product.getId());
            values.put("userId", userId);
            values.put("SoLg", 1);

            db.insert("Cart", null, values);
        }
        cursor.close();
        db.close();
    }

    public void deleteCartItem(String userId, String productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "userId = ? AND productId = ?";
        String[] selectionArgs = {userId, productId};

        db.delete("Cart", selection, selectionArgs);
        db.close();
    }

    public void updateSoLg(String userId, String productId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("SoLg", quantity);

        String selection = "userId = ? AND productId = ?";
        String[] selectionArgs = {userId, productId};

        db.update("Cart", values, selection, selectionArgs);
        db.close();
    }
}
