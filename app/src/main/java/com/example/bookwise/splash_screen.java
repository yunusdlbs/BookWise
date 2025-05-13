package com.example.bookwise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookwise.utils.TypeWriter;

public class splash_screen extends AppCompatActivity {

    private static final int IMAGE_DELAY = 1500; // 1.5 saniye sonra kitaplar gelsin
    private static final int SPLASH_DURATION = 3000; // 3 saniye sonra geçiş olsun
    private static final int TEXT_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);

        ImageView bookImage = findViewById(R.id.bookImage);
        TextView titleText = findViewById(R.id.titleText);

        TypeWriter typeWriter = findViewById(R.id.typeWriter);
        typeWriter.setCharacterDelay(150); // Harfler arası gecikme
        typeWriter.animateText("BookWise");

        new Handler().postDelayed(() -> {
            startActivity(new Intent(splash_screen.this, login.class));
            finish();
        }, 3000);
    }
}