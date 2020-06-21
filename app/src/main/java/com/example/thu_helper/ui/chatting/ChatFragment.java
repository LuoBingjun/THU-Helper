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

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatFragment extends Fragment {

    private ListView mListView;
    private List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
    private ChatListViewAdapter mAdapter;
    private Button sendBtn;
    private EditText inputText;
    private ChatViewModel mViewModel;

    private String user_id;
    private String other_id;

    private String message;

    private ChatWebSocketClient client;
    private LoggedInUser loggedInUser;
    private ChatMsgEntity entity;

    public static ChatFragment newInstance() { return new ChatFragment();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_chat, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        mViewModel.getMessages().setValue(msgList);
        mAdapter = new ChatListViewAdapter(root.getContext(),R.layout.msg_item,mViewModel.getMessages().getValue());
        inputText = root.findViewById(R.id.input_text);
        sendBtn = root.findViewById(R.id.sendMsgBtn);
        mListView = root.findViewById(R.id.msg_list_view);
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
                    ChatMsgEntity msg = new ChatMsgEntity(user_id,dateFormat.format(date),content,ChatMsgEntity.MSG_SEND);
                    msgList.add(msg);
                    mListView.setSelection(msgList.size());//将ListView定位到最后一行
                    inputText.setText("");

                    JSONObject jsonMessage =new JSONObject();
                    try {
                        jsonMessage.put("other",other_id);
                        jsonMessage.put("msg",content);
                        jsonMessage.put("type","message");
                        message = jsonMessage.toString();
                        sendMessage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        new ChatTask().execute();
        return root;
    }

    private void connect(){
        client = new ChatWebSocketClient(URI.create(Global.ws_url)){
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                try {    //String转JSONObject
                    JSONObject result = new JSONObject(message);
                    if(result.get("type").equals("message")) {
                        Date date = new Date();
                        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                        String sender = (String) result.get("sender");
                        String receivedMsg = (String) result.get("msg");
                        ChatMsgEntity msg = new ChatMsgEntity(sender,dateFormat.format(date),receivedMsg, ChatMsgEntity.MSG_RECEIVED);
                        msgList.add(msg);
                        mViewModel.getMessages().postValue(msgList);
                        //mViewModel.setReceivedMsg(msg);
                    }
                     }
                    catch (JSONException e) { e.printStackTrace();}
            }
        };
        try {
            client.connectBlocking();
            JSONObject jsonMessage =new JSONObject();
            jsonMessage.put("token",loggedInUser.token);
            System.out.println(jsonMessage);
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
                System.out.println(message);
            }
            else {
                client.reconnectBlocking();
                client.send(message);
            }
        }
        else {
            client = new ChatWebSocketClient(URI.create(Global.ws_url));
            client.connectBlocking();
            client.send(message);
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
        user_id = loggedInUser.username;
        connect();

        messages.observe(this,new Observer<List<ChatMsgEntity>>() {
            @Override
            public void onChanged(List<ChatMsgEntity> chatMsgEntities) {
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(msgList.size());
            }
        });
    }

        private class ChatTask extends AsyncTask<Void, Void, Result<Boolean>> {

            @Override
            protected Result<Boolean> doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.url_prefix + "/chat/off_msg")
                            .addHeader("Authorization", "Token " + loggedInUser.token)
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONArray res = null;
                        res = new JSONArray(response.body().string());
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject result = res.getJSONObject(i);
                            if (result.get("type").equals("message")) {
                                Date date = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                                String sender = (String) result.get("sender");
                                String receivedMsg = (String) result.get("msg");
                                ChatMsgEntity msg = new ChatMsgEntity(sender, dateFormat.format(date), receivedMsg, ChatMsgEntity.MSG_RECEIVED);
                                msgList.add(msg);
                            }
                        }
                        mViewModel.getMessages().postValue(msgList);
                        return new Result.Success<>(true);
                    }
                    return new Result.Error(new Exception("请求失败，请联系网站管理员"));
                } catch (Exception e) {
                        return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
                    }
            }
        }
}
