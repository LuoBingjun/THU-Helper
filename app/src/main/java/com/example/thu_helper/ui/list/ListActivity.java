package com.example.thu_helper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.thu_helper.ui.list.ListFragment;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ListFragment.newInstance())
                    .commitNow();
        }
    }
}