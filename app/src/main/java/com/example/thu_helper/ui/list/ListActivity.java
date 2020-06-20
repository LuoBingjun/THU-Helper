package com.example.thu_helper.ui.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.thu_helper.R;
import com.example.thu_helper.ui.detail.DetailViewModel;
import com.example.thu_helper.ui.list.ListFragment;

public class ListActivity extends AppCompatActivity {
    ListViewModel listViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        int type = getIntent().getIntExtra("type", 0);
        listViewModel.setType(type);
        switch(type){
            case 0:
                setTitle("等待中订单");
                break;
            case 1:
                setTitle("进行中订单");
                break;
            case 2:
                setTitle("历史订单");
                break;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ListFragment.newInstance())
                    .commitNow();
        }
    }
}