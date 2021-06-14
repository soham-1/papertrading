package com.example.papertrading;

public class StocksOwned {
    private String company;
    private int qty;
    private int avg_amt;

    public StocksOwned() {}

    public StocksOwned(String company, int qty, int avg_amt) {
        this.company = company;
        this.qty = qty;
        this.avg_amt = avg_amt;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getAvg_amt() {
        return avg_amt;
    }

    public void setAvg_amt(int avg_amt) {
        this.avg_amt = avg_amt;
    }

    @Override
    public String toString() {
        return "StocksOwned{" +
                "company='" + company + '\'' +
                ", qty=" + qty +
                ", avg_amt=" + avg_amt +
                '}';
    }
}
