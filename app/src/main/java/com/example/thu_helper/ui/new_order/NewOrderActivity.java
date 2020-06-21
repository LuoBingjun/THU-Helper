package com.example.thu_helper.ui.new_order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.thu_helper.R;

public class NewOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, OrderFragment.newInstance())
                    .commitNow();
        }
    }
}