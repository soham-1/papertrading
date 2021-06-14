package com.example.papertrading;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class stock_details extends AppCompatActivity {
    private Dbhandler handler;
    private String company;
    private String curr_price;
    private String pct_change;
    private int qty_val;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        handler = new Dbhandler(this, "Trading", null, 1);
        Intent intent = getIntent();
        company = intent.getStringExtra("company");
        curr_price = intent.getStringExtra("curr_price");
        pct_change = intent.getStringExtra("pct_change");
        Log.d("mytag", company);
        Log.d("mytag", curr_price);
        Log.d("mytag", pct_change);

        TextView curr_price_val = findViewById(R.id.curr_price_val);
        TextView pct_change_val = findViewById(R.id.pct_change_val);
        TextView qty_owned_val  = findViewById(R.id.qty_val);
        TextView total_amount_val  = findViewById(R.id.amount_val);
        Button buy = findViewById(R.id.buy);
        Button sell = findViewById(R.id.sell);

        curr_price_val.setText(curr_price);
        pct_change_val.setText(pct_change);
        StocksOwned so = handler.getStocksOwned(company.toUpperCase());
        if (so.getCompany() != null) {
            qty_owned_val.setText(String.valueOf(so.getQty()));
            total_amount_val.setText(String.valueOf(so.getQty()*so.getAvg_amt()));
            qty_val = so.getQty();
        } else {
            qty_owned_val.setText("0");
            total_amount_val.setText("0");
            sell.setEnabled(false);
        }

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy_slip();
            }
        });
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sell_slip();
            }
        });
    }

    public void buy_slip() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.buy_sell_slip);
        dialog.setCancelable(true);
        TextView title = dialog.findViewById(R.id.qty_stocks_text);
        title.setText("Qty of stocks to buy");
        dialog.show();
        ImageButton button = dialog.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText symbol = dialog.findViewById(R.id.qty_stocks);
                String qty = symbol.getText().toString();
                if (qty.equals("")) buy_slip();
                else {
                    int _qty = (int) Math.ceil(Double.parseDouble(qty));
                    int _curr_price = (int) Math.ceil(Double.parseDouble(curr_price));
                    Transaction tr = new Transaction(0, _qty, company, _curr_price, 1, 0, new Date());
                    handler.addTransaction(tr);
                    Log.d("mytag", symbol.getText().toString());
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    public void sell_slip() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.buy_sell_slip);
        dialog.setCancelable(true);
        TextView title = dialog.findViewById(R.id.qty_stocks_text);
        title.setText("Qty of stocks to sell");
        dialog.show();
        ImageButton button = dialog.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText symbol = dialog.findViewById(R.id.qty_stocks);
                String qty = symbol.getText().toString();
                if (qty.equals("")) sell_slip();
                else if (Integer.parseInt(qty) > qty_val) sell_slip();
                else {
                    int _qty = (int) Math.ceil(Double.parseDouble(qty));
                    int _curr_price = (int) Math.ceil(Double.parseDouble(curr_price));
                    Transaction tr = new Transaction(0, _qty, company, _curr_price, 0, 0, new Date());
                    handler.addTransaction(tr);
                    Log.d("mytag", symbol.getText().toString());
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

}