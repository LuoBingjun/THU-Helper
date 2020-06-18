package com.example.thu_helper.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.thu_helper.R;
import com.example.thu_helper.ui.setting.SettingFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SettingFragment.newInstance())
                    .commitNow();
        }
    }
}