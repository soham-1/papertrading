package com.example.papertrading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class Portfolio extends AppCompatActivity {
    private Dbhandler handler;
    List<StocksOwned> so_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        handler = new Dbhandler(this, "Trading", null, 1);
        LinearLayout table = findViewById(R.id.portfolio_table);
        so_list = handler.getAllStocksOwned();
        for (StocksOwned so : so_list) {
            View view = getLayoutInflater().inflate(R.layout.inflate_protfolio, null);
            TextView Company = view.findViewById(R.id.Stocks_val);
            TextView Qty = view.findViewById(R.id.Qty_val);
            TextView Average = view.findViewById(R.id.Average_val);

            Company.setText(so.getCompany());
            Qty.setText(String.valueOf(so.getQty()));
            Average.setText(String.valueOf(so.getAvg_amt()));

            table.addView(view);
        }
    }
}