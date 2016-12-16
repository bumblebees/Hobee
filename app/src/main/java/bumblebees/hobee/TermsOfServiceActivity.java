package bumblebees.hobee;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class TermsOfServiceActivity extends AppCompatActivity {
    private ImageView hobeeLogo;
    private TextView termsOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        hobeeLogo = (ImageView) findViewById(R.id.hobeeLogo);
        termsOne  = (TextView) findViewById(R.id.termsOne);

    }
}
