package com.example.quaternion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button buttonMainRotate, buttonMainInterpolate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMainRotate = findViewById(R.id.buttonMainRotate);
        buttonMainInterpolate = findViewById(R.id.buttonMainInterpolate);

        buttonMainRotate.setOnClickListener(view -> {
            Log.d(TAG, "buttonMainRotate clicked");
            Intent intent = new Intent(MainActivity.this, ActivityRotate.class);
            startActivity(intent);
        });

        buttonMainInterpolate.setOnClickListener(view -> {
            Log.d(TAG, "buttonMainInterpolate clicked");
            Intent intent = new Intent(MainActivity.this, ActivityInterpolate.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}