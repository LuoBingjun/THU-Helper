package com.example.thu_helper.ui.chatting;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatClient extends WebSocketClient {

    public ChatClient(URI serverUri) {
        super(serverUri);
}

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("socket connecting....");
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
