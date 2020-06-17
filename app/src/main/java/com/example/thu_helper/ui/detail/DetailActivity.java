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
        int id = getIntent().getIntExtra("id", 0);
//        detailViewModel.getId().setValue(id);
        RecordDetail record = new RecordDetail(id, "帮忙取快递" + id,
                "紫荆14号楼后小树林", new Date(), "最近不在学校，求帮忙取快递" + id);
        detailViewModel.getRecord().setValue(record);
    }
}
