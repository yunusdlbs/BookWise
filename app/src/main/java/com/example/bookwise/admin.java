package com.example.bookwise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin);

        Button btnEditUsers = findViewById(R.id.btnEditUsers);
        Button btnAddBook = findViewById(R.id.btnAddBook);
        Button btnEditBook = findViewById(R.id.btnEditBook);
        Button btnDeleteBook = findViewById(R.id.btnDeleteBook);

        btnEditUsers.setOnClickListener(v -> {
            // kullanıcı düzenleme ekranına yönlendir
            Toast.makeText(this, "Kullanıcı düzenleme geliyor...", Toast.LENGTH_SHORT).show();
        });

        btnAddBook.setOnClickListener(v -> {
           // startActivity(new Intent(this, AddBookActivity.class));
        });

        btnEditBook.setOnClickListener(v -> {
            Toast.makeText(this, "Kitap düzenleme geliyor...", Toast.LENGTH_SHORT).show();
        });

        btnDeleteBook.setOnClickListener(v -> {
            Toast.makeText(this, "Kitap silme geliyor...", Toast.LENGTH_SHORT).show();
        });

    }
}