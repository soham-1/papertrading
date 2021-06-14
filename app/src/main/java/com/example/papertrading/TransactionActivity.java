package com.example.papertrading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {
    private Dbhandler handler;
    private LinearLayout transaction_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        handler = new Dbhandler(this, "Trading", null, 1);
        transaction_list = (LinearLayout) findViewById(R.id.transaction_list);
        fillTransaction();
    }

    public void fillTransaction() {
        List<Transaction> tr_list = handler.getAllTransactions();

        for (Transaction tr : tr_list) {
            View view = getLayoutInflater().inflate(R.layout.inflate_transaction, null, false);
            TextView date = view.findViewById(R.id.row_date);
            TextView comp = view.findViewById(R.id.row_stock);
            TextView status = view.findViewById(R.id.row_status);
            TextView amount = view.findViewById(R.id.row_amount);
            TextView qty = view.findViewById(R.id.row_qty);
            TextView gain_loss = view.findViewById(R.id.row_gain_loss);

            if (tr.getStatus() == 0) {
                if (tr.getGain_loss() < 0) {
                    date.setTextColor(Color.GREEN);
                    comp.setTextColor(Color.GREEN);
                    status.setTextColor(Color.GREEN);
                    amount.setTextColor(Color.GREEN);
                    qty.setTextColor(Color.GREEN);
                    gain_loss.setTextColor(Color.GREEN);
                } else {
                    date.setTextColor(Color.RED);
                    comp.setTextColor(Color.RED);
                    status.setTextColor(Color.RED);
                    amount.setTextColor(Color.RED);
                    qty.setTextColor(Color.RED);
                    gain_loss.setTextColor(Color.RED);
                }
                gain_loss.setText(String.valueOf(tr.getGain_loss()));
                status.setText("SELL");
            } else {
                gain_loss.setText("0");
                status.setText("BUY");
            }
            comp.setText(tr.getComp());
            amount.setText(String.valueOf(tr.getUnit_amount()));
            qty.setText(String.valueOf(tr.getQty()));
            date.setText(getDateTime(tr.getDate()));

            transaction_list.addView(view);
        }
    }

    public String getDateTime(Date d) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(d);
    }
}