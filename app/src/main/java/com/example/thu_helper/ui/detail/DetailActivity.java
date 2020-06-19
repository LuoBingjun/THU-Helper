package com.example.thu_helper.ui.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.thu_helper.R;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    DetailViewModel detailViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DetailFragment.newInstance())
                    .commitNow();
        }

        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        String id = getIntent().getStringExtra("id");
        detailViewModel.getId().setValue(id);
    }
}
