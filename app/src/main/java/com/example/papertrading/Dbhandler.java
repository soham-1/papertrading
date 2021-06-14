package com.example.papertrading;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
         String create_transaction = "CREATE TABLE IF NOT EXISTS " + TABLE_transaction + " (id INTEGER PRIMARY KEY AUTOINCREMENT, qty INTEGER, comp TEXT, unit_amount INTEGER, status INTEGER, gain_loss Integer DEFAULT 0, date DATETIME DEFAULT CURRENT_TIMESTAMP)";
         String create_balance = "CREATE TABLE IF NOT EXISTS " + TABLE_balance + " (username TEXT PRIMARY KEY, password TEXT, balance INTEGER)";
         String create_stocksOwned = "CREATE TABLE IF NOT EXISTS " + TABLE_stocksOwned + " (comp TEXT PRIMARY KEY, qty INTEGER, average_amount INTEGER)";
         db.execSQL(create_fav);
         db.execSQL(create_transaction);
         db.execSQL(create_balance);
         db.execSQL(create_stocksOwned);

         ContentValues values = new ContentValues();
         values.put("username", "soham");
         values.put("password", "soham");
         values.put("balance", 10000);
//         db.execSQL("INSERT INTO " + TABLE_balance + " (username, password, balance) VALUES ('soham', 'soham', 10000)");
         long k = db.insert(TABLE_balance, null, values);
         Log.d("mytag", "balance added: " + Long.toString(k));
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
        Log.d("mytag", "fav added: " + Long.toString(k));
        db.close();
    }

    public Favourites getFavourite(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_favourites + " WHERE name = '" + name + "' COLLATE NOCASE", null);
        Favourites fv = new Favourites();
        fv.setName(cursor.getString(cursor.getColumnIndex("name")));
        db.close();
        return fv;
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
        return db.delete("favourites", "name = '" + stock_name + "'", null) > 0;
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
        values.put("balance", ab.getBalance());
        long k = db.insert(TABLE_balance, null, values);
        Log.d("mytag", "balance added; " + Long.toString(k));
        db.close();
    }

    public int getBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT balance FROM " + TABLE_balance + " WHERE username = 'soham'", null);
        cursor.moveToFirst();
        int balance = cursor.getInt(cursor.getColumnIndexOrThrow("balance"));
        return balance;
    }

    public void updateBalance(AccountBalance ab) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", ab.getUsername());
        values.put("password", ab.getPassword());
        values.put("balance", ab.getBalance());
        db.update(TABLE_balance, values, "username=?", new String[] {ab.getUsername()});
    }

    public void addTransaction(Transaction tr) {
        if (tr.getStatus() == 1) {
            if (checkBalance(tr.getQty(), tr.getUnit_amount())) { // deduct from balance
                SQLiteDatabase db = this.getWritableDatabase();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues values = new ContentValues();
                values.put("qty", tr.getQty());
                values.put("comp", tr.getComp());
                values.put("unit_amount", tr.getUnit_amount());
                values.put("status", tr.getStatus());
                values.put("date", dateFormat.format(tr.getDate()));
                long k = db.insert(TABLE_transaction, null, values);
                Log.d("mytag", "transaction added with status buy: " + Long.toString(k));
                if (k != -1) {
                    long check = addStocksOwned(tr.getComp().toUpperCase(), tr.getQty(), tr.getUnit_amount());
                    if (check == -1) db.delete(TABLE_transaction, "id=?", new String[]{String.valueOf(check)});
                    else updateBalance(new AccountBalance("soham", "soham", getBalance()-(tr.getUnit_amount() * tr.getQty())));
                }
                db.close();
            }
        } else { // add to balance
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StocksOwned stocksOwned = getStocksOwned(tr.getComp().toUpperCase());
            SQLiteDatabase db = this.getWritableDatabase();
            int gain_loss = (tr.getUnit_amount() - stocksOwned.getAvg_amt()) * tr.getQty();
            ContentValues values = new ContentValues();
            values.put("qty", tr.getQty());
            values.put("comp", tr.getComp());
            values.put("unit_amount", tr.getUnit_amount());
            values.put("status", tr.getStatus());
            values.put("date", dateFormat.format(tr.getDate()));
            values.put("gain_loss", gain_loss);
            long k = db.insert(TABLE_transaction, null, values);
            Log.d("mytag", "transaction added with status sell: " + Long.toString(k));
            if (k != -1) {
                long check = updateStocksOwned(tr.getComp().toUpperCase(), tr.getQty());
                if (check == -1) db.delete(TABLE_transaction, "id=?", new String[]{String.valueOf(check)});
                else updateBalance(new AccountBalance("soham", "soham", getBalance()+(tr.getUnit_amount() * tr.getQty())));
            }
            db.close();
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> ls = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_transaction, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Transaction tr = new Transaction();
                tr.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                tr.setQty(cursor.getInt(cursor.getColumnIndexOrThrow("qty")));
                tr.setComp(cursor.getString(cursor.getColumnIndexOrThrow("comp")));
                tr.setUnit_amount(cursor.getInt(cursor.getColumnIndexOrThrow("unit_amount")));
                tr.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
                tr.setGain_loss(cursor.getInt(cursor.getColumnIndexOrThrow("gain_loss")));
                Date date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date")) * 1000);
                tr.setDate(date);
                ls.add(tr);
                cursor.moveToNext();
            }
        } else {
            Log.d("mytag", "no transactions");
        }
        cursor.close();
        db.close();
        Collections.sort(ls, new SortByDate());
        return ls;
    }

    public long deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long k = db.delete(TABLE_transaction, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return k;
    }

    public boolean checkBalance(int qty, int amount) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql_get_user = "SELECT * FROM " + TABLE_balance + " where username = 'soham'";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_balance + " where username = 'soham'", null);
        int balance;
        if (cursor.moveToFirst()) balance = cursor.getInt(cursor.getColumnIndexOrThrow("balance"));
        else balance = 0;
        int total = qty * amount;
        cursor.close();
        db.close();
        if (total > balance - 2000) return false;
        return true;
    }

    public long addStocksOwned(String comp, int qty, int unit_amount) {
        // stock name should always be in upper case for this table
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_get_stocks = "SELECT * FROM " + TABLE_stocksOwned + " WHERE comp = '" + comp.toUpperCase() + "'";
        Cursor cursor = db.rawQuery(sql_get_stocks, null);
        ContentValues values = new ContentValues();
        values.put("comp", comp.toUpperCase());
        values.put("qty", qty);
        values.put("average_amount", unit_amount);
        if (cursor.moveToFirst()) {
            int prev_qty = cursor.getInt(cursor.getColumnIndexOrThrow("qty"));
            int prev_amount = cursor.getInt(cursor.getColumnIndexOrThrow("average_amount"));
            int new_avg = (prev_amount + (unit_amount * qty)) / (qty + prev_qty);
            values.put("average_amount", new_avg);
            values.put("qty", prev_qty+qty);
            return db.update(TABLE_stocksOwned, values, "comp=?", new String[] {comp.toUpperCase()});
        }
        return db.insert(TABLE_stocksOwned, null, values);
    }

    public long updateStocksOwned(String comp, int qty) {
        StocksOwned so = getStocksOwned(comp);
        if (so.getQty() - qty == 0) return deleteStocksOwned(comp);
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("comp", comp);
            values.put("qty", so.getQty() - qty);
            values.put("average_amount", so.getAvg_amt());
            long k = db.update(TABLE_stocksOwned, values, "comp = ?", new String[] {comp});
            db.close();
            return k;
        }
    }

    public StocksOwned getStocksOwned(String cmp) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_stocksOwned + " WHERE comp = '" + cmp.toUpperCase() + "'", null);
        StocksOwned so = new StocksOwned();
        if (cursor.moveToFirst()) {
            so.setAvg_amt(cursor.getInt(cursor.getColumnIndexOrThrow("average_amount")));
            so.setCompany(cursor.getString(cursor.getColumnIndexOrThrow("comp")));
            so.setQty(cursor.getInt(cursor.getColumnIndexOrThrow("qty")));
        }
        db.close();
        return so;
    }

    public List<StocksOwned> getAllStocksOwned() {
        List<StocksOwned> ls = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_stocksOwned, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                StocksOwned so = new StocksOwned();
                so.setAvg_amt(cursor.getInt(cursor.getColumnIndexOrThrow("average_amount")));
                so.setCompany(cursor.getString(cursor.getColumnIndexOrThrow("comp")));
                so.setQty(cursor.getInt(cursor.getColumnIndexOrThrow("qty")));
                ls.add(so);
                cursor.moveToNext();
            }
        } else {
            Log.d("mytag", "no stocks owned by user");
        }
        cursor.close();
        db.close();
        return ls;
    }

    public long deleteStocksOwned(String comp) {
        SQLiteDatabase db = this.getWritableDatabase();
        long k = db.delete(TABLE_stocksOwned, "comp = ?", new String[] {comp});
        db.close();
        return k;
    }

}