package com.example.thu_helper.ui.chatting;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.detail.DetailViewModel;
import com.example.thu_helper.ui.detail.RecordDetail;
import com.example.thu_helper.utils.Global;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.interfaces.IWebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

public class ChatFragment extends Fragment {

    private ListView mListView;
    private List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
    private ChatListViewAdapter mAdapter;
    private Button sendBtn;
    private EditText inputText;
    private String name;
    private ChatViewModel mViewModel;

    private String publisher_id;
    private String other_id;

    private String message;

    private ChatWebSocketClient client;
    private LoggedInUser loggedInUser;

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

        //test chat
        //connect();

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

                    JSONObject jsonMessage =new JSONObject();
                    try {
                        jsonMessage.put("other",other_id);
                        jsonMessage.put("msg",content);
                        message = jsonMessage.toString();
                        sendMessage();
                    } catch (JSONException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }catch (Exception e){
                        System.out.println(String.format("Exception class: %s, %s",e.getClass(),e.getMessage()));
                        e.printStackTrace();
                    }
                }
            }
        });
        new ChatTask().execute();
        return root;
    }

    private void connect(){
        client = new ChatWebSocketClient(URI.create(Global.ws_url));
        try {
            client.connectBlocking();
            JSONObject jsonMessage =new JSONObject();
            jsonMessage.put("token",loggedInUser.token);
            client.send(jsonMessage.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessage() throws InterruptedException {
        if(client != null) {
            if(client.isOpen()){
                client.send(message);
            }
            else {
                client.reconnectBlocking();
                client.send(message);
                System.out.println(String.format("null client connectBlocking1: %s",client.isOpen()));
            }
        }
        else {
            client = new ChatWebSocketClient(URI.create(Global.ws_url));
            client.connectBlocking();
            client.send(message);
            System.out.println(String.format("null client connectBlocking2: %s",client.isOpen()));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MutableLiveData<List<ChatMsgEntity>> messages = mViewModel.getMessages();
        loggedInUser = LoginRepository.getInstance().getUser();
        other_id = getArguments().getString("other_id");
        publisher_id = loggedInUser.username;
        connect();
        //        client = new ChatWebSocketClient(URI.create(Global.ws_url));
        //        try {
        //            client.connectBlocking();
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

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

        private class ChatTask extends AsyncTask<Void, Void, Result<Boolean>>{

        @Override
        protected Result<Boolean> doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/chat/off_msg")
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    //JSONObject res = new JSONObject(response.body().string());
                    System.out.println(String.format("off_message: %s",response.body().string()));
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                System.out.println(String.format("Exception: %s, Class: %s",e.getMessage(),e.getClass()));
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }
    }
}
