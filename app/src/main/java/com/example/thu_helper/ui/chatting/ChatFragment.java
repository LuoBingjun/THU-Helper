package com.example.thu_helper.ui.chatting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thu_helper.R;
import com.example.thu_helper.ui.detail.DetailViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {

    private ListView mListView;
    private List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
    private ChatListViewAdapter mAdapter;
    private Button sendBtn;
    private EditText inputText;
    private String name;
    private ChatViewModel mViewModel;

    public static ChatFragment newInstance() { return new ChatFragment();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_chat, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        initMsg();
        mViewModel.getMessages().setValue(msgList);
        mAdapter = new ChatListViewAdapter(root.getContext(),R.layout.msg_item,mViewModel.getMessages().getValue());
        inputText = root.findViewById(R.id.input_text);
        sendBtn = root.findViewById(R.id.sendMsgBtn);
        mListView = root.findViewById(R.id.msg_list_view);
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
                    msgList.add(msg);
                    mListView.setSelection(msgList.size());//将ListView定位到最后一行
                    inputText.setText("");
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MutableLiveData<List<ChatMsgEntity>> messages = mViewModel.getMessages();
        messages.observe(this,new Observer<List<ChatMsgEntity>>() {
            @Override
            public void onChanged(List<ChatMsgEntity> chatMsgEntities) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initMsg(){
        ChatMsgEntity msg1 = new ChatMsgEntity("张三","6-17 21:09","吃了吗？"
                ,ChatMsgEntity.MSG_RECEIVED);
        msgList.add(msg1);

        ChatMsgEntity msg2 = new ChatMsgEntity("李四","6-17 21:11","还没，你呢？"
                ,ChatMsgEntity.MSG_SEND);
        msgList.add(msg2);

        ChatMsgEntity msg3 = new ChatMsgEntity("张三","6-17 21:12","我吃了。"
                ,ChatMsgEntity.MSG_RECEIVED);
        msgList.add(msg3);
    }
}
