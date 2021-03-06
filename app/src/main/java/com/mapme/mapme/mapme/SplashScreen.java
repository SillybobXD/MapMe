package com.mapme.mapme.mapme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView logo = findViewById(R.id.iv_logo_splash);
        final ImageView location = findViewById(R.id.iv_location_splash);

        YoYo.with(Techniques.BounceInRight).duration(2800).playOn(logo);
        YoYo.with(Techniques.Hinge).duration(2000).delay(1000).playOn(location);

        Thread mThread = new Thread() {

            @Override
            public void run() {
                super.run();
                {
                    try {
                        sleep(2800);
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
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
