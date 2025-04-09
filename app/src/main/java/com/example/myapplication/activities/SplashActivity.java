package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        // 🌀 אנימציה עם חזרה לגודל המקורי (bounce effect)
        logo.setScaleX(1f);
        logo.setScaleY(1f);
        logo.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> logo.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .start());

        // המשך ל-MainActivity אחרי 4 שניות
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 4000);
    }
}