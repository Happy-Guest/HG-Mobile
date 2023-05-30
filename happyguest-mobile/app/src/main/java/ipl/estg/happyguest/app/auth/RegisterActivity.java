package ipl.estg.happyguest.app.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ipl.estg.happyguest.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}