package com.mapme.mapme.mapme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.SharedPreferencesManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView logo = findViewById(R.id.iv_logo_splash);
        final ImageView location = findViewById(R.id.iv_location_splash);
        final ImageView flag = findViewById(R.id.iv_flag_splash);

        SharedPreferencesManager.init(this);

        YoYo.with(Techniques.BounceInRight).duration(2800).playOn(logo);
        YoYo.with(Techniques.FadeOutUp).duration(2000).delay(1000).playOn(location);
        YoYo.with(Techniques.FadeInLeft).duration(1700).delay(1000).playOn(flag);

        Thread mThread = new Thread() {

            @Override
            public void run() {
                super.run();
                {
                    try {
                        sleep(2800);
                        Intent intent = new Intent(SplashScreen.this, MapActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        };

        mThread.start();
    }
}
