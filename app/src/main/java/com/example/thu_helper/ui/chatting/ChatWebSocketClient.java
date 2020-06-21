package com.example.thu_helper.ui.chatting;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatWebSocketClient extends WebSocketClient {


    public ChatWebSocketClient(URI serverUri) {
        super(serverUri);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println(ex.getMessage());
    }

    @Override
    public void send(String text) {
        super.send(text);
    }

}
