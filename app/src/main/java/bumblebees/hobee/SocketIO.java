package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

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

    static void checkIfExists(final String username, final Context packageContext, final String extras) {

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("user_login", username, new Ack() {
                    @Override
                    public void call(Object... objects) {
                        String res = (String)objects[0];
                        if (res.equals("REGISTER")) {
                            Intent intent = new Intent(packageContext, RegisterUserActivity.class);
                            intent.putExtra("login", extras);
                            packageContext.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(packageContext, HomeActivity.class);
                            intent.putExtra("login", extras);
                            packageContext.startActivity(intent);
                        }
                        socket.disconnect();
                    }
                });
            }
        });
        socket.connect();
    }

    static void register(final JSONObject jsonObject, final Context packageContext){

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("test", jsonObject);
                socket.disconnect();
            }
        });
        socket.connect();
        Intent intent = new Intent(packageContext, HobbyActivity.class);
        try {
            intent.putExtra("user_ID", jsonObject.getString("loginID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        packageContext.startActivity(intent);
    }
}
