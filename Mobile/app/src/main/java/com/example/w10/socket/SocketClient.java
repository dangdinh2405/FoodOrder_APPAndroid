package com.example.w10.socket;

import android.util.Log;

import java.net.URI;
import java.util.function.Consumer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class SocketClient extends WebSocketClient {
    private Consumer<String> onMsg;
    private String param;
    public SocketClient(URI serverUri, Consumer<String> onMsg) {
        super(serverUri);
        this.onMsg = onMsg;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("ws", "Connected");
        this.send(this.param);
    }

    @Override
    public void onMessage(String message) {
        Log.i("ws", "Message: " + message);
        this.onMsg.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("ws", "Connection closed");
        Thread thr = new Thread(this::reconnect);
        thr.start();
    }

    @Override
    public void onError(Exception ex) {
        Log.e("ws", "Error: " + ex.getMessage());
    }
}
