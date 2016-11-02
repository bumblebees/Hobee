package bumblebees.hobee;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import io.socket.client.Socket;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private SignInButton googleLoginButton;
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mainActivity();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //GOOGLE SIGN IN
        //Request default and email
        GoogleSignInOptions googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignIn)
                .build();

        googleLoginButton = (SignInButton) findViewById(R.id.google_login_button);
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
                GoogleSignInAccount acc = res.getSignInAccount();

                //TODO: Check if the user exists and decide where to go from there, for now just go to next activity
               Intent registerIntent = new Intent(this, RegisterUser.class);
                Bundle userData = new Bundle();
                userData.putString("first_name", acc.getGivenName());
                userData.putString("last_name", acc.getFamilyName());
                userData.putString("username", acc.getDisplayName());
                userData.putString("email", acc.getEmail());
                userData.putString("photo", String.valueOf(acc.getPhotoUrl()));
                registerIntent.putExtra("userData", userData);
                this.startActivity(registerIntent);


            }
            else{
                Log.d("acc", "sign-in failed");
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void mainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
