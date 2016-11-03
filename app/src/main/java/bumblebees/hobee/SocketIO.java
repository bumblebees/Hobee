package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class SocketIO {

    static private Socket socket;

    static private String url = "http://129.16.155.22";
    static private String port = ":3001";

    static void start(){
        try {
            socket = IO.socket(url+port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static void checkIfExists(final String username, final Context packageContext) {

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("user_login", username, new Ack() {
                    @Override
                    public void call(Object... objects) {
                        Intent intent = new Intent(packageContext, TestActivity.class);
                        packageContext.startActivity(intent);
                    }
                });
                //socket.disconnect();
            }
        });
        socket.connect();
    }
}
