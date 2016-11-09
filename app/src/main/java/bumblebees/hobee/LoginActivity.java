package bumblebees.hobee;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    CallbackManager callbackManager;
    LoginButton loginButton;
    SignInButton googleLoginButton;
    Socket socket;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: remove after testing
        String s = getApplicationContext().getSharedPreferences("HobeeSessionPreferences", 0).getString("id", null);
        Log.d("SHARED_PREF", s);


        SocketIO.start();
        session = new SessionManager(getApplicationContext());
        //session.checkLogin();

        /*
            FACEBOOK SIGN IN
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("user_birthday","user_photos","email","user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SocketIO.checkIfExists(AccessToken.getCurrentAccessToken(), LoginActivity.this);
            }
            @Override
            public void onCancel() { }
            @Override
            public void onError(FacebookException error) { }
        });




        /*
            GOOGLE SIGN IN
            Request default and email
        */
        //TODO: also request gender and birthday


        GoogleSignInOptions googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignIn)
                .build();

        googleLoginButton = (SignInButton) findViewById(R.id.google_login_button);
        googleLoginButton.setSize(SignInButton.SIZE_WIDE);
        googleLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, 9001);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 9001){
            GoogleSignInResult res = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(res.isSuccess()){
                //Sign-in success
                final GoogleSignInAccount acc = res.getSignInAccount();

                //Check if the user exists in the DB
                try {
                    socket = IO.socket("http://129.16.155.22:3001");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        socket.emit("user_login", acc.getId(), new Ack() {
                            @Override
                            public void call(Object... objects) {
                                String res = (String)objects[0];
                                //User exists in the database, move to login screen
                                if(res.equals("LOGIN")){
                                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    homeIntent.putExtra("user_ID", acc.getId());
                                    LoginActivity.this.startActivity(homeIntent);

                                }
                                //User does not exist in the database, start registering
                                else if(res.equals("REGISTER")){
                                    Intent registerIntent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                                    Bundle userData = new Bundle();
                                    userData.putString("id", acc.getId());
                                    userData.putString("first_name", acc.getGivenName());
                                    userData.putString("last_name", acc.getFamilyName());
                                    userData.putString("email", acc.getEmail());
                                    userData.putString("photo", String.valueOf(acc.getPhotoUrl()));
                                    registerIntent.putExtra("login", "google");
                                    registerIntent.putExtra("userData", userData);
                                    LoginActivity.this.startActivity(registerIntent);
                                }
                                else{
                                    Log.d("socket", "received unknown reply");
                                }
                                socket.disconnect();
                            }
                        });

                    }
                });
                socket.connect();


            }
            else{
                Log.d("acc", "sign-in failed");
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("acc", connectionResult.toString());
    }
}
