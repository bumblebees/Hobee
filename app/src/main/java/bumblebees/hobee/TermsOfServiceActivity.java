package bumblebees.hobee;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class TermsOfServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        ImageView hobeeLogo = (ImageView) findViewById(R.id.hobeeLogo);
        TextView termsOne = (TextView) findViewById(R.id.termsOne);

    }
}
