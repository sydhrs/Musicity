package com.students.fyp.emotionrecognition.Activities;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.students.fyp.emotionrecognition.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getUid() != null)
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finishAffinity();
            }
        }, 1600);
    }
}