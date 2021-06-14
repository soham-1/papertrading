package com.example.papertrading;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {
    private Dbhandler handler;
    private TextView balance_amt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        handler = new Dbhandler(this, "Trading", null, 1);
        TextView username = findViewById(R.id.username);
        balance_amt = findViewById(R.id.balance_amt);
        username.setText("soham");
        setbal();
        Button button = findViewById(R.id.addBalance);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_bal();
            }
        });
    }

    public void setbal() {
        balance_amt.setText(String.valueOf(handler.getBalance()));
    }

    public void add_bal() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.inflate_balance);
        dialog.setCancelable(true);
        dialog.show();
        ImageButton button1 = dialog.findViewById(R.id.addbal);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText bal = dialog.findViewById(R.id.amt);
                int bal_int = Integer.parseInt(bal.getText().toString());
                if (bal_int == 0) add_bal();
                else {
                    handler.updateBalance(new AccountBalance("soham", "soham", handler.getBalance()+bal_int));
                    dialog.dismiss();
                    setbal();
                }
            }
        });
    }
}
