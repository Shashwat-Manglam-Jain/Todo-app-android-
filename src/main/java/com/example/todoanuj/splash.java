package com.example.todoanuj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window window=getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN // Hide the status bar
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Keep the layout stable
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Optional: Hides navigation bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Optional: Hides navigation bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Makes status and navigation bars appear when swiped
        );

        // Delay of 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the splash activity
        }, 3000); // 3000 milliseconds = 3 seconds
    }
}
