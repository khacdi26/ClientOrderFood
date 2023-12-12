package com.example.dacnapp.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {
    public dbHelper(@Nullable Context context) {
        super(context, "appDoAnCNCART", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCart = String.format("CREATE TABLE %s (" +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s INTEGER, " +
                        "PRIMARY KEY (%s, %s))",
                "Cart", "productId", "userId", "SoLg", "productId", "userId");
        db.execSQL(queryCart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String query = "DROP TABLE Cart";
            db.execSQL(query);
            onCreate(db);
        }
    }
}
