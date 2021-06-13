package com.example.papertrading;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Dbhandler extends SQLiteOpenHelper {
    public static final String TABLE_favourites = "favourites";
    public static final String TABLE_transaction = "transactions";
    public static final String TABLE_balance = "balance";
    public static final String TABLE_stocksOwned = "stocksOwned";

    public Dbhandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String create_fav = "CREATE TABLE " + TABLE_favourites + " (name TEXT PRIMARY KEY)";
         String create_transaction = "CREATE TABLE IF NOT EXISTS "+ TABLE_transaction +" (id INTEGER PRIMARY KEY AUTOINCREMENT, qty INTEGER, comp TEXT, unit_amount INTEGER, status INTEGER, date DATETIME DEFAULT CURRENT_TIMESTAMP)";
         String create_balance = "CREATE TABLE IF NOT EXISTS "+ TABLE_balance +" (username TEXT PRIMARY KEY, password TEXT, balance INTEGER)";
         String create_stocksOwned = "CREATE TABLE IF NOT EXISTS "+ TABLE_stocksOwned +" (comp TEXT PRIMARY KEY, qty INTEGER, average_amount INTEGER)";
         db.execSQL(create_fav);
         db.execSQL(create_transaction);
         db.execSQL(create_balance);
         db.execSQL(create_stocksOwned);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_favourites);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_transaction);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_balance);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_stocksOwned);
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

    public void addBalance(AccountBalance ab) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", ab.getUsername());
        values.put("password", ab.getPassword());
        values.put("balance", ab.getPassword());
        long k = db.insert(TABLE_balance, null, values);
        Log.d("mytag", Long.toString(k));
        db.close();
    }

    public void updateBalance(AccountBalance ab) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", ab.getUsername());
        values.put("password", ab.getPassword());
        values.put("balance", ab.getPassword());
        db.update(TABLE_balance, values, "username=?", new String[] {ab.getUsername()});
    }

    public void addTransaction(Transaction tr) {
        if (checkBalance(tr.getQty(), tr.getUnit_amount())) {
            SQLiteDatabase db = this.getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put("qty", tr.getQty());
            values.put("comp", tr.getComp());
            values.put("unit_amount", tr.getUnit_amount());
            values.put("status", tr.getStatus());
            values.put("date", dateFormat.format(tr.getDate()));
            long k = db.insert(TABLE_transaction,null, values);
            Log.d("mytag", Long.toString(k));
            if (k != -1) {
                long check = addStocksOwned(tr.getComp(), tr.getQty(), tr.getUnit_amount());
                if (check == -1) db.delete(TABLE_transaction, "id=?", new String[] {String.valueOf(check)});
            }
            db.close();
        }
    }

    public boolean checkBalance(int qty, int amount) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql_get_user = "SELECT balance FROM " + TABLE_balance + " where username = " + "soham";
        Cursor cursor = db.rawQuery(sql_get_user, null);
        int balance = cursor.getInt(cursor.getColumnIndex("balance"));
        int total = qty * amount;
        db.close();
        if (total < balance - 2000) return false;
        return true;
    }

    public long addStocksOwned(String comp, int qty, int unit_amount) {
        // stock name should always be in upper case for this table
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_get_stocks = "SELECT * FROM " + TABLE_stocksOwned + " WHERE comp = " + comp.toUpperCase();
        Cursor cursor = db.rawQuery(sql_get_stocks, null);
        ContentValues values = new ContentValues();
        values.put("comp", comp.toUpperCase());
        values.put("qty", qty);
        values.put("average_amount", unit_amount);
        if (cursor.moveToFirst()) {
            int prev_qty = cursor.getInt(cursor.getColumnIndex("qty"));
            int prev_amount = cursor.getInt(cursor.getColumnIndex("average_amount"));
            int new_avg = (prev_amount + (unit_amount * qty)) / (qty + prev_qty);
            values.put("average_amount", new_avg);
            return db.update(TABLE_stocksOwned, values, "comp=?", new String[] {comp.toUpperCase()});
        }
        return db.insert(TABLE_stocksOwned, null, values);
    }

}
