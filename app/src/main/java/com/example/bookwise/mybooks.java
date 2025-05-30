package com.example.bookwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookwise.adapters.MyBooksPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class mybooks extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    MyBooksPagerAdapter adapter;
    Button btnhome;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybooks);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnhome = findViewById(R.id.btnhome);

        adapter = new MyBooksPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 🔥 Intent'ten gelen sekme pozisyonu
        int selectedTab = getIntent().getIntExtra("selected_tab", 0); // default: Favoriler
        viewPager.setCurrentItem(selectedTab, false); // false: animasyonsuz geçiş

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Favoriler" : "Ödünç Aldıklarım")
        ).attach();

        btnhome.setOnClickListener(v -> {
            Intent intent = new Intent(mybooks.this, home.class);
            startActivity(intent);
        });
    }
}
