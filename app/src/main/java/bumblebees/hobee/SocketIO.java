package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

class SocketIO {

    static private Socket socket;

    static private Bundle userData;

    static void start(){
        try {
            socket = IO.socket("http://129.16.155.22:3001");
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
                            SessionManager session = new SessionManager(context);
                            session.createSession(accessToken.getUserId(), "facebook");
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

    static void checkIfExists(final GoogleSignInAccount account, final Context context){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("user_exists", account.getId(), new Ack() {
                    @Override
                    public void call(Object... objects) {
                        if (!(Boolean)objects[0]) {

                            userData = new Bundle();
                            userData.putString("loginId", account.getId());
                            userData.putString("origin", "google");
                            userData.putString("firstName", account.getGivenName());
                            userData.putString("lastName", account.getFamilyName());
                            userData.putString("birthday", "");
                            userData.putString("email", account.getEmail());
                            userData.putString("gender", "");
                            Uri uri = account.getPhotoUrl();
                            if (uri != null) {
                                userData.putString("pic", uri.toString());
                            }
                            else {
                                userData.putString("pic", "");
                            }

                            Intent intent = new Intent(context, RegisterUserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userData", userData);
                            context.startActivity(intent);
                        }
                        else {
                            SessionManager session = new SessionManager(context);
                            session.createSession(account.getId(), "google");
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

    /**
     * Get all the events that the user is involved in.
     * @param userID - ID that is being searched for
     * @param option - "host" or "joined" or "pending"
     */
    static void getEvents(final String userID, final String option, final Ack callback){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = new JSONObject();
                try {
                    data.put("userID", userID);
                    data.put("option", option);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("get_events", data, callback);
            }
        });
        socket.connect();
    }

    static public void disconnect(){
        socket.disconnect();
    }
}
