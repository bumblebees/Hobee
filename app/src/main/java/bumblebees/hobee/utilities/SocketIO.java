package bumblebees.hobee.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import bumblebees.hobee.HobbyActivity;
import bumblebees.hobee.HomeActivity;
import bumblebees.hobee.RegisterUserActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import bumblebees.hobee.UserProfileActivity;
import bumblebees.hobee.objects.User;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SocketIO {

    static private SocketIO instance;

    private Socket socket;
    private Bundle userData;
    private Gson gson;


    public static SocketIO getInstance() {
        if (instance == null) {
            synchronized (SocketIO.class) {
                if (instance == null) {
                    instance = new SocketIO();
                }
            }
        }
        return instance;
    }


    /**
     * Empty constructor
     */
    private SocketIO() {

    }


    /**
     * Setup socket connection
     */
    public void start() {
        if (socket == null) {
            try {
                socket = IO.socket("http://129.16.155.22:3001");
                socket.connect();
                gson = new Gson();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check with database if user exists, if not, retrieve user data from facebook account
     * and pass it to user registration activity.
     * If exists, get user data, save it to shared preferences and go to home screen
     *
     * @param accessToken user data from facebook API
     * @param context     context from which method is called
     */
    public void checkIfExists(final AccessToken accessToken, final Context context) {
        socket.emit("user_exists", accessToken.getUserId(), new Ack() {
            @Override
            public void call(Object... objects) {
                if (!(Boolean) objects[0]) {

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
                } else {
                    SocketIO.getInstance().getUserAndLogin(accessToken.getUserId(), context);
                }

            }
        });
    }


    /**
     * Check with database if user exists, if not, retrieve user data from google account
     * and pass it to user registration activity.
     * If exists, get user data, save it to shared preferences and go to home screen
     *
     * @param account user data from google API
     * @param context context from which method is called
     */
    public void checkIfExists(final GoogleSignInAccount account, final Context context) {
        socket.emit("user_exists", account.getId(), new Ack() {
            @Override
            public void call(Object... objects) {
                if (!(Boolean) objects[0]) {

                    userData = new Bundle();
                    userData.putString("loginId", account.getId());
                    userData.putString("origin", "google");
                    userData.putString("firstName", account.getGivenName());
                    userData.putString("lastName", account.getFamilyName());
                    userData.putString("birthday", null);
                    userData.putString("email", account.getEmail());
                    userData.putString("gender", null);
                    Uri uri = account.getPhotoUrl();
                    if (uri != null) {
                        userData.putString("pic", uri.toString());
                    } else {
                        userData.putString("pic", "");
                    }

                    Intent intent = new Intent(context, RegisterUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userData", userData);
                    context.startActivity(intent);
                } else {
                    SocketIO.getInstance().getUserAndLogin(account.getId(), context);
                }

            }
        });
    }


    /**
     * Send user data to server to be saved in database and go to HobbyActivity
     *
     * @param user     contains user data
     * @param packageContext context from which method is called
     */
    public void register(final User user, String imageString, final Context packageContext) {

        final JSONObject userImage = new JSONObject();
        try {
            userImage.put("userId", user.getUserID());
            userImage.put("imageString", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("save_image", userImage);
        socket.emit("register_user", gson.toJson(user));

        Intent intent = new Intent(packageContext, HobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        packageContext.startActivity(intent);
    }

//    public void sendImage(String userId, String imageString){
//
//        final JSONObject userImage = new JSONObject();
//        try {
//            userImage.put("userId", userId);
//            userImage.put("imageString", imageString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//            @Override
//            public void call(Object... objects) {
//                socket.emit("save_image", userImage);
//                socket.disconnect();
//            }
//        });
//        socket.connect();
//    }


    /**
     * Get user data from database and save it to user class
     *
     * @param loginId google or fb login
     */
    public void getUserAndLogin(final String loginId, final Context context) {
        socket.emit("get_user", loginId, new Ack() {
            @Override
            public void call(Object... objects) {
                JSONObject userJSON = (JSONObject) objects[0];
                User user = gson.fromJson(String.valueOf(userJSON), User.class);

                Profile.getInstance().setUser(user);
                Log.d("event", user.toString());

                SessionManager session = new SessionManager(context);
                session.setPreferences(user.getLoginId(), user.getOrigin());

                Intent intent = new Intent(context, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void getUserAndOpenProfile(final String UUID, final Context context){
        socket.emit("get_userUUID", UUID, new Ack() {
            @Override
            public void call(Object... objects) {
                JSONObject userJSON = (JSONObject) objects[0];
                Intent intent = new Intent(context, UserProfileActivity.class);
                if(userJSON != null) {
                    User user = gson.fromJson(String.valueOf(userJSON), User.class);
                    intent.putExtra("User",gson.toJson(user));
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                }
        });
    }

    /**
     * Retrieve the events that the user is hosting.
     * @param userID
     * @param ack
     */
    public void getHostedEvents(String userID, Ack ack){
        socket.emit("get_hosted_events", userID, ack);
    }

    /**
     * Retrieve the events that the user has been accepted to.
     * @param userID
     * @param ack
     */
    public void getAcceptedEvents(String userID, Ack ack){
        socket.emit("get_accepted_events", userID, ack);
    }

    /**
     * Retrieve the events that the user is waiting on.
     * @param userID
     * @param ack
     */
    public void getPendingEvents(String userID, Ack ack){
        socket.emit("get_pending_events", userID, ack);
    }
}
