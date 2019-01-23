package app.nunc.com.staatsoperlivestreaming.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;

import app.nunc.com.staatsoperlivestreaming.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }, 2000);
    }

}
