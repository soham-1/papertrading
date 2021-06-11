package com.example.papertrading;

import java.text.SimpleDateFormat;

public class Transaction {
    private int id;
    private int qty;
    private String comp;
    private int unit_amount;
    private int status; // 1 for buy, 0 for sell
    private SimpleDateFormat date;

    public Transaction(int id, int qty, String comp, int unit_amount, int status, SimpleDateFormat date) {
        this.id = id;
        this.qty = qty;
        this.comp = comp;
        this.unit_amount = unit_amount;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getComp() {
        return comp;
    }

    public void setComp(String comp) {
        this.comp = comp;
    }

    public int getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(int unit_amount) {
        this.unit_amount = unit_amount;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public SimpleDateFormat getDate() {
        return date;
    }

    public void setDate(SimpleDateFormat date) {
        this.date = date;
    }
}
