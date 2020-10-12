package com.example.boolan.service;

import okhttp3.WebSocket;
import okio.ByteString;

public interface IWsManager {
    WebSocket getWebSocket();

    void startConnect();

    void stopConnect();

    boolean isWsConnected();

    int getCurrentStatus();

    void setCurrentStatus(int currentStatus);

    boolean sendMessage(String sender,String receiver,String ms);

    boolean sendMessage(ByteString byteString);

    boolean sendMessage(String sender,String ms);
}
