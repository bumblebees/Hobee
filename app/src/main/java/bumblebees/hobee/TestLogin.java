package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TestLogin extends AppCompatActivity {

    private EditText username;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);

       // username = (EditText) findViewById(R.id.username);

        loginButton = (Button) findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestLogin.this, TestUser.class);
                intent.putExtra("key", username.getText().toString());
                TestLogin.this.startActivity(intent);
            }
        });
    }
}
