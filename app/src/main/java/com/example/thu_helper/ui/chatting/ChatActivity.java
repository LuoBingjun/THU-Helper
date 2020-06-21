package com.example.thu_helper.ui.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thu_helper.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        String other_id = getIntent().getStringExtra("other_id");
        if(savedInstanceState == null){
            Bundle bundle = new Bundle();
            bundle.putString("other_id",other_id);
            ChatFragment chatFragment = ChatFragment.newInstance();
            chatFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chat_container,chatFragment)
                    .commitNow();
        }

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

        requirePermission();
    }

    @AfterPermissionGranted(1)
    private void requirePermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        String[] permissionsForQ = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION, //target为Q时，动态请求后台定位权限
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= 29 ? EasyPermissions.hasPermissions(this, permissionsForQ) :
                EasyPermissions.hasPermissions(this, permissions)) {
//            Toast.makeText(this, "权限OK", Toast.LENGTH_LONG).show();
        } else {
            EasyPermissions.requestPermissions(this, "需要权限",
                    1, Build.VERSION.SDK_INT >= 29 ? permissionsForQ : permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}