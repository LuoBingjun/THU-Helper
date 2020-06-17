package com.example.thu_helper.ui.chatting;

import androidx.appcompat.app.AppCompatActivity;

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

    private ListView mListView;
    private List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
    private ChatListViewAdapter mAdapter;
    private Button sendBtn;
    private EditText inputText;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        initMsg();
        mAdapter = new ChatListViewAdapter(this,R.layout.msg_item,msgList);
        inputText = findViewById(R.id.input_text);
        sendBtn = findViewById(R.id.sendMsgBtn);
        mListView = findViewById(R.id.msg_list_view);
        name = "张三";
        mListView.setAdapter(mAdapter);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!content.equals("")){
                    Date date = new Date();
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                    ChatMsgEntity msg = new ChatMsgEntity(name,dateFormat.format(date),content,ChatMsgEntity.MSG_SEND);
                    Toast toast = Toast.makeText(v.getContext(),dateFormat.format(date),Toast.LENGTH_SHORT);
                    msgList.add(msg);
                    toast.show();
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(msgList.size());//将ListView定位到最后一行
                    inputText.setText("");
                }
            }
        });
    }

    private void initMsg(){
        ChatMsgEntity msg1 = new ChatMsgEntity("张三","6-17 21:09","吃了吗？"
                ,ChatMsgEntity.MSG_ACCEPT);
        msgList.add(msg1);

        ChatMsgEntity msg2 = new ChatMsgEntity("李四","6-17 21:11","还没，你呢？"
                ,ChatMsgEntity.MSG_SEND);
        msgList.add(msg2);

        ChatMsgEntity msg3 = new ChatMsgEntity("张三","6-17 21:12","我吃了。"
                ,ChatMsgEntity.MSG_ACCEPT);
        msgList.add(msg3);
    }
}
