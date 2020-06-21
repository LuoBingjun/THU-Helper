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
import android.widget.Toast;

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

import org.java_websocket.client.WebSocketClient;
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
    private String name;
    private ChatViewModel mViewModel;

    private String publisher_id;
    private String other_id;

    private String message;

    private ChatWebSocketClient client;
    private LoggedInUser loggedInUser;

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

        mFrameLayout = root.findViewById(R.id.frameLayout);
        initLocation();
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
                System.out.println(message);
            }
            else {
                client.reconnectBlocking();
                client.send(message);
                System.out.println(String.format("client connectBlocking1: %s",client.isOpen()));
            }
        }
        else {
            client = new ChatWebSocketClient(URI.create(Global.ws_url));
            client.connectBlocking();
            client.send(message);
            System.out.println(String.format("client connectBlocking2: %s",client.isOpen()));
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
        other_id = getArguments().getString("other_id");
        publisher_id = loggedInUser.username;
        connect();

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

        setOtherLocation(40.042893, 116.269673, "北京市");
    }

    private void setMyLocation(double latitude, double longtitude, String name) {
        LatLng position = new LatLng(latitude, longtitude);
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

    private void setOtherLocation(double latitude, double longtitude, String name){
        LatLng position = new LatLng(latitude,longtitude);
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
        //其中 locationChangeListener 为 LocationSource.active 返回给用户的位置监听器
        //用户通过这个监听器就可以设置地图的定位点位置
//        if(i == TencentLocation.ERROR_OK && mLocationChangedListener != null){
//            Location location = new Location(tencentLocation.getProvider());
//            //设置经纬度
//            location.setLatitude(tencentLocation.getLatitude());
//            location.setLongitude(tencentLocation.getLongitude());
//            //设置精度，这个值会被设置为定位点上表示精度的圆形半径
//            location.setAccuracy(tencentLocation.getAccuracy());
//            //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
//            location.setBearing((float) tencentLocation.getBearing());
//            //将位置信息返回给地图
//            mLocationChangedListener.onLocationChanged(location);
//        }
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
