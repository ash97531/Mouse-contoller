package com.kridacreations.mouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class KridaScreen extends AppCompatActivity {
    // The initial screen that is displayed when the app is opened
    // Splash screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_krida_screen);

        getSupportActionBar().hide();

        Handler handler = new Handler();
        final Intent[] i = new Intent[1];
        Runnable myRunnable = new Runnable() {
            public void run() {
                i[0] = new Intent(KridaScreen.this, MainActivity.class);
                startActivity(i[0]);
                finish();
            }
        };

        handler.postDelayed(myRunnable, 1000);
    }
}