package com.example.papertrading;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Transaction {
    private int id;
    private int qty;
    private String comp;
    private int unit_amount;
    private int status; // 1 for buy, 0 for sell
    private int gain_loss;
    private Date date;

    public Transaction() {}

    public Transaction(int id, int qty, String comp, int unit_amount, int status, int gain_loss, Date date) {
        this.id = id;
        this.qty = qty;
        this.comp = comp;
        this.unit_amount = unit_amount;
        this.status = status;
        this.gain_loss = gain_loss;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGain_loss() { return gain_loss; }

    public void setGain_loss(int gain_loss) { this.gain_loss = gain_loss; }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

class SortByDate implements Comparator<Transaction> {
    @Override
    public int compare(Transaction o1, Transaction o2) {
        return Long.valueOf(o2.getDate().getTime()).compareTo(Long.valueOf(o1.getDate().getTime()));
    }
}
