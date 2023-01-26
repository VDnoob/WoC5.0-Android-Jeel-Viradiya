package com.example.unifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.SystemClock;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class StartActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(StartActivity.this, StartPageActivity.class));
                    finish();
                }
            }
        },2000);

    }

}