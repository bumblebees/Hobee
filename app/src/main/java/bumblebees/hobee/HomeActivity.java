package bumblebees.hobee;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SocketIO.start();

        TextView text = (TextView) findViewById(R.id.textView);

        SocketIO.checkIfExists("109673242154927933461", HomeActivity.this);
    }
}
