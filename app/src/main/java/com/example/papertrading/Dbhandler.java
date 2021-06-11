package com.example.papertrading;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Dbhandler extends SQLiteOpenHelper {
    private String TABLE_transaction;
  
    public Dbhandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String create_fav = "CREATE TABLE favourites (name TEXT PRIMARY KEY)";
         String create_transac = "CREATE TABLE IF NOT EXISTS "+ TABLE_transaction +" (id INT PRIMARY KEY, qty INT, comp TEXT, unit_amount INT, status INT, date DATETIME DEFAULT CURRENT_TIMESTAMP)";
         db.execSQL(create_fav);
         db.execSQL(create_transac);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favourites");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_transaction);
        onCreate(db);
    }

    public void addFavourite(Favourites fv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", fv.getName());
        long k = db.insert("favourites", null, values);
        Log.d("mytag", Long.toString(k));
        db.close();
    }

    public void getFavourite(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("favourites", new String[]{"name"}, "name=?", new String[]{name}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("mytag", cursor.getString(0));
        } else {
            Log.d("mytag", "error");
        }
        db.close();
    }

    public List<String> getAllFavourites() {
        List<String> stock_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("favourites", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                stock_list.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                cursor.moveToNext();
            }
        } else {
            Log.d("mytag", "no favourite stocks to watch");
        }
        cursor.close();

        db.close();
        return stock_list;
    }

    public boolean deleteFavourites(String stock_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("favourites", "name" + "=" + stock_name, null) > 0;
    }

    public List<String> deleteAllFavourites(List<String> stock_names) {
        /**
         * accepts a list of strings to be deleted
         * returns list of rows not deleted
         */
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> ls = new ArrayList<String>();
        for (String stock_name : stock_names) {
            int deleted = db.delete("favourites", "name=?", new String[]{stock_name});
            if (deleted == 0) ls.add(stock_name);
        }
        return ls;
    }

}
