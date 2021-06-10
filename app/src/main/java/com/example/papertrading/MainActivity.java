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
    private final String token = "/quote?token=sk_2e5ca37dd8b6477a8d04c50954ddeed3";
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
            case R.id.stocks_owned:
                Toast.makeText(this, "stocks", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Dbhandler(this, "Trading", null, 1);
        stock_list = handler.getAllFavourites();
        favourite_stocks = findViewById(R.id.favourite_stocks);


//        populateList();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_stocks();
            }
        });

        Log.d("mytag", handler.getAllFavourites().toString());

        handler.close();
    }

    public void populateList() {
        Log.d("mytag", stock_list.toString());

        for(int i = 0; i<2; i++) {
            View view = getLayoutInflater().inflate(R.layout.stock_list, null);
            TextView company = view.findViewById(R.id.company_name);
            TextView curr_price = view.findViewById(R.id.curr_price);
            TextView pct_change = view.findViewById(R.id.pct_change);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, base + stock_list.get(i) + token,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d("mytag", response);


                            JSONObject result = null;
                            try {
                                result = new JSONObject(response);
                                company.setText(result.getString("symbol"));
                                curr_price.setText(result.getString("iexRealtimePrice"));
                                float changePercent = Float.parseFloat(result.getString("changePercent"));
                                pct_change.setText(String.format("%.2f", changePercent) + "%");

                                if (changePercent < 0) pct_change.setTextColor(Color.RED);
                                else pct_change.setTextColor(Color.GREEN);
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
                handler.addFavourite(new Favourites(symbol.getText().toString()));
                Log.d("mytag", symbol.getText().toString());
                dialog.dismiss();
                finish();
                startActivity(getIntent());
            }
        });
    }
}