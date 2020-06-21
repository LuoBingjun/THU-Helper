package com.example.thu_helper.ui.chatting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

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

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"Chatting", Toast.LENGTH_SHORT).show();
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
    }

}