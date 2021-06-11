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

public class stock_details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        Intent intent = getIntent();
        String company = intent.getStringExtra("company");
        String curr_price = intent.getStringExtra("curr_price");
        String pct_change = intent.getStringExtra("pct_change");
//        String symbol = "AAPL";
        Log.d("mytag", company);
        Log.d("mytag", curr_price);
        Log.d("mytag", pct_change);
        TextView curr_price_val = findViewById(R.id.curr_price_val);
        curr_price_val.setText(curr_price);
        TextView pct_change_val = findViewById(R.id.pct_change_val);
        pct_change_val.setText(pct_change);

        Button buy = findViewById(R.id.buy);
        Button sell = findViewById(R.id.sell);
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
                String cmp_name = symbol.getText().toString();
//                if (cmp_name.equals("")) add_stocks();
//                else {
//                    handler.addFavourite(new Favourites(cmp_name));
//                    Log.d("mytag", symbol.getText().toString());
//                    dialog.dismiss();
//                    finish();
//                    startActivity(getIntent());
//                }
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
                String cmp_name = symbol.getText().toString();
//                if (cmp_name.equals("")) add_stocks();
//                else {
//                    handler.addFavourite(new Favourites(cmp_name));
//                    Log.d("mytag", symbol.getText().toString());
//                    dialog.dismiss();
//                    finish();
//                    startActivity(getIntent());
//                }
            }
        });
    }

}