package com.example.w10.socket;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class NewsSocket {
    private SocketClient wsClient;
    private URI uri;
    public NewsSocket(String url){
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            Log.e("ws", "Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public void connect(Consumer<String> onMsg) {
        this.wsClient = new SocketClient(this.uri, onMsg);
        this.wsClient.connect();
    }

}
