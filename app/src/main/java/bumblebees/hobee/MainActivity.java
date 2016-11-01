package bumblebees.hobee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

// hey, how are ya?

public class MainActivity extends AppCompatActivity {
    private TextView fbName;
    private TextView fbEmail;
    private TextView fbGender;
    private TextView fbAge;
    private ImageView fbImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fbName   = (TextView) findViewById(R.id.userName);
        fbEmail  =  (TextView) findViewById(R.id.userEmail);
        fbGender = (TextView) findViewById(R.id.userGender);
        fbAge    = (TextView) findViewById(R.id.fbage);
        fbImage  = (ImageView) findViewById(R.id.fbImage);
        if (AccessToken.getCurrentAccessToken() == null){
            loginActivity();
        }

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        fbName.setText(object.optString("name"));
                        fbEmail.setText(object.optString("email"));
                        fbGender.setText(object.optString("gender"));
                        fbAge.setText(object.optString("age_range"));
                        Bitmap bitmap = BitmapFactory.decodeFile(object.optString("url"));
                        fbImage.setImageBitmap(bitmap);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,age_range,picture");
        request.setParameters(parameters);
        request.executeAsync();

    }



    private void loginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}