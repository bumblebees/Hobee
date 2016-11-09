package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketIO {

    static private Socket socket;
    static private String url = "http://129.16.155.22:3001";

    static private Bundle userData;

    static void start(){
        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static void checkIfExists(final AccessToken accessToken, final Context context) {

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("user_exists", accessToken.getUserId(), new Ack() {
                    @Override
                    public void call(Object... objects) {
                        if (!(Boolean)objects[0]) {

                            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    userData = new Bundle();
                                    try {
                                        userData.putString("loginId", accessToken.getUserId());
                                        userData.putString("origin", "facebook");
                                        userData.putString("firstName", object.getString("first_name"));
                                        userData.putString("lastName", object.getString("last_name"));
                                        userData.putString("birthday", object.getString("birthday"));
                                        userData.putString("email", object.getString("email"));
                                        userData.putString("gender", object.getString("gender"));
                                        //userData.putString("pic", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                        userData.putString("pic", object.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(context, RegisterUserActivity.class);
                                    intent.putExtra("userData", userData);
                                    context.startActivity(intent);
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,gender,birthday,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                        else {
                            Intent intent = new Intent(context, HomeActivity.class);
                            context.startActivity(intent);
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
        packageContext.startActivity(intent);
    }
}
