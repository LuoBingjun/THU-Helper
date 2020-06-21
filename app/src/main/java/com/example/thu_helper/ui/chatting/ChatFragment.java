package com.example.thu_helper.ui.chatting;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.utils.Global;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.IOverlay;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.TencentMapGestureListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatFragment extends Fragment implements TencentLocationListener {

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

    private FrameLayout mFrameLayout;
    private TencentLocationManager mLocationManager;
    private TencentLocationRequest mLocationRequest;
    private TencentMap mTencentMap;
    private Marker mOtherMarker = null;
    private Marker mMyMarker = null;
    private boolean dragged = false;

    public static ChatFragment newInstance() { return new ChatFragment();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_chat, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        mViewModel.getMessages().setValue(msgList);
        other_id = getArguments().getString("other_id");
        mAdapter = new ChatListViewAdapter(root.getContext(),R.layout.msg_item,mViewModel.getMessages().getValue(),other_id);
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

        mFrameLayout = root.findViewById(R.id.frameLayout);
        initLocation();
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
                    else if (result.get("type").equals("location")) {
                        ChatViewModel.LocationData locationData = new ChatViewModel.LocationData(result);
                        mViewModel.getOtherLocation().postValue(locationData);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
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
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MutableLiveData<List<ChatMsgEntity>> messages = mViewModel.getMessages();
        loggedInUser = LoginRepository.getInstance().getUser();
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

    private void initLocation() {
        // Init
        mLocationManager = TencentLocationManager.getInstance(getActivity().getApplicationContext());
        mLocationRequest = TencentLocationRequest.create();
        mLocationRequest.setInterval(3000);

        TextureMapView mapView = new TextureMapView(getContext());
        mFrameLayout.addView(mapView);
        mTencentMap = mapView.getMap();

        mLocationManager.requestLocationUpdates(mLocationRequest, this);

        mTencentMap.setOnMapClickListener(new TencentMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                dragged = true;
            }
        });

        mTencentMap.addTencentMapGestureListener(new TencentMapGestureListener() {
            @Override
            public boolean onDoubleTap(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public boolean onSingleTap(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public boolean onFling(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public boolean onScroll(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public boolean onLongPress(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public boolean onDown(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public boolean onUp(float v, float v1) {
                dragged = true;
                return false;
            }

            @Override
            public void onMapStable() {
                dragged = false;
            }
        });

        mViewModel.getOtherLocation().observe(this, new Observer<ChatViewModel.LocationData>() {
            @Override
            public void onChanged(ChatViewModel.LocationData locationData) {
                setOtherLocation(locationData.latitude, locationData.longitude, locationData.name);
            }
        });
    }

    private void setMyLocation(double latitude, double longitude, String name) {
        LatLng position = new LatLng(latitude, longitude);
        if (mMyMarker == null){
            dragged = false;
            MarkerOptions options = new MarkerOptions(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_red))
                    .anchor(0.5f, 1);
            mMyMarker = mTencentMap.addMarker(options);
        }
        mMyMarker.setPosition(position);

        if (!dragged) {
            List<IOverlay> overlays = new ArrayList<>();
            overlays.add(mMyMarker);
            if(mOtherMarker != null) {
                overlays.add(mOtherMarker);
            }
            mTencentMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    mTencentMap.calculateZoomToSpanLevel(
                            overlays, null,
                            0, 0, 0, 0)));
        }
    }

    private void setOtherLocation(double latitude, double longitude, String name){
        LatLng position = new LatLng(latitude,longitude);
        if (mOtherMarker == null){
            dragged = false;
            MarkerOptions options = new MarkerOptions(position)
                    .title("对方")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue))
                    .anchor(0.5f, 1);
            mOtherMarker = mTencentMap.addMarker(options);
            mOtherMarker.showInfoWindow();
        }
        mOtherMarker.setPosition(position);
        mOtherMarker.setSnippet(name);

        if(!dragged){
            List<IOverlay> overlays = new ArrayList<>();
            overlays.add(mOtherMarker);
            if(mMyMarker != null) {
                overlays.add(mMyMarker);
            }
            mTencentMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    mTencentMap.calculateZoomToSpanLevel(
                            overlays, null,
                            0, 0, 0, 0)));
        }
    }


    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("longitude", tencentLocation.getLongitude());
            jsonMessage.put("latitude", tencentLocation.getLatitude());
            jsonMessage.put("name", tencentLocation.getName());
            jsonMessage.put("other", other_id);
            jsonMessage.put("type","location");
            message = jsonMessage.toString();
            sendMessage();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

        setMyLocation(tencentLocation.getLatitude(), tencentLocation.getLongitude(), tencentLocation.getName());
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }


    private class ChatTask extends AsyncTask<Void, Void, Result<Boolean>>{
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
                            else if (result.get("type").equals("location")) {
                                mViewModel.getOtherLocation().postValue(new ChatViewModel.LocationData(result));
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
