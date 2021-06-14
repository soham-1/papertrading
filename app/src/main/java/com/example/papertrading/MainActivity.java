package com.example.papertrading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String base = "https://cloud.iexapis.com/stable/stock/";
    private final String token = "/quote?token=" + BuildConfig.API_KEY;
    private List<String> stock_list;
    private LinearLayout favourite_stocks;
    private FloatingActionButton fab;
    private Dbhandler handler;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu:
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                Intent account_intent = new Intent(this, Profile.class);
                startActivity(account_intent);
                return true;
            case R.id.portfolio_menu:
                Toast.makeText(this, "Portfolio", Toast.LENGTH_SHORT).show();
                Intent portfolio_intent = new Intent(this, Portfolio.class);
                startActivity(portfolio_intent);
                return true;
            case R.id.transactions:
                Toast.makeText(this, "Transaction", Toast.LENGTH_SHORT).show();
                Intent transaction_intent = new Intent(this, TransactionActivity.class);
                startActivity(transaction_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Dbhandler(this, "Trading", null, 1);
        favourite_stocks = findViewById(R.id.favourite_stocks);

        populateList();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_stocks();
            }
        });

        Log.d("mytag", "list of all favourites " + handler.getAllFavourites().toString());

        handler.close();
    }

    public void populateList() {
        stock_list = handler.getAllFavourites();

        for(int i = 0; i<stock_list.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.stock_list, null);
            TextView company = view.findViewById(R.id.company_name);
            TextView full_name = view.findViewById(R.id.full_name);
            TextView curr_price = view.findViewById(R.id.curr_price);
            TextView pct_change = view.findViewById(R.id.pct_change);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, base + stock_list.get(i) + token,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject result;
                            try {
                                result = new JSONObject(response);
                                company.setText(result.getString("symbol"));
                                String name = result.getString("companyName");
                                if (name.length() > 25) name = name.substring(0, 25) + "...";
                                full_name.setText(name);
                                curr_price.setText(result.getString("latestPrice"));
                                float changePercent = Float.parseFloat(result.getString("changePercent"));
                                pct_change.setText(String.format("%.2f", changePercent) + "%");

                                if (changePercent < 0) {
                                    pct_change.setTextColor(Color.RED);
                                    curr_price.setTextColor(Color.RED);
                                }
                                else {
                                    pct_change.setTextColor(Color.GREEN);
                                    curr_price.setTextColor(Color.GREEN);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    company.setText("Couldn't load data!");
                }
            });

//          Add the request to the RequestQueue.
            queue.add(stringRequest);

            favourite_stocks.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open_activity_stock_details(company.getText().toString(), curr_price.getText().toString(), pct_change.getText().toString());
                }
            });
//            view.setOnTouchListener(new );
        }
    }

    public void add_stocks() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_stock_dialog);
        dialog.setCancelable(true);
        dialog.show();
        ImageButton button = dialog.findViewById(R.id.add_to_list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText symbol = dialog.findViewById(R.id.company_symbol);
                String cmp_name = symbol.getText().toString();
                if (cmp_name.equals("")) add_stocks();
                else {
                    handler.addFavourite(new Favourites(cmp_name));
                    Log.d("mytag", "fav_symbol: " + symbol.getText().toString());
                    dialog.dismiss();
//                    finish();
//                    startActivity(getIntent());
                    populateList();
                }
            }
        });
    }

    public void open_activity_stock_details(String company, String curr_price, String pct_change) {
        Intent stock_intent = new Intent(this, stock_details.class);
        stock_intent.putExtra("company", company);
        stock_intent.putExtra("curr_price", curr_price);
        stock_intent.putExtra("pct_change", pct_change);
        startActivity(stock_intent);
    }
}