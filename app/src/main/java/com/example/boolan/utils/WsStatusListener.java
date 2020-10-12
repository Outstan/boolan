package com.example.boolan.utils;

import okhttp3.Response;
import okio.ByteString;

public class WsStatusListener {

    public void onReconnect() {
    }

    public void onOpen(Response response) {
    }

    public void onMessage(ByteString bytes) {
    }

    public void onMessage(String text) {
    }

    public void onClosing(int code, String reason) {
    }

    public void onClosed(int code, String reason) {
    }

    public void onFailure(Throwable t, Response response) {
    }
}
