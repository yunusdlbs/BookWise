package com.example.bookwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView tvUsername, tvEmail, tvIsAdmin, tvFavoriteCount, tvBorrowedCount;
    private Button btnHome;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvUserEmail);
        tvIsAdmin = findViewById(R.id.tvIsAdmin);
        tvFavoriteCount = findViewById(R.id.tvFavoriteCount);
        tvBorrowedCount = findViewById(R.id.tvBorrowedCount);
        btnHome = findViewById(R.id.btnHome);

        if (user != null) {
            tvEmail.setText("E-posta: " + user.getEmail());

            String uid = user.getUid();

            // Kullanıcı verisi
            db.collection("Users").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String username = doc.getString("username");
                        Boolean isAdmin = doc.getBoolean("isAdmin");

                        tvUsername.setText("Kullanıcı: " + (username != null ? username : "-"));
                        tvIsAdmin.setText("Admin: " + (Boolean.TRUE.equals(isAdmin) ? "Evet" : "Hayır"));
                    });

            // Favoriler sayısı
            db.collection("Users").document(uid)
                    .collection("Favorites")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        int count = snapshot.size();
                        tvFavoriteCount.setText("Favori Kitap Sayısı: " + count);
                    });

            // Ödünç alınanlar sayısı
            db.collection("Users").document(uid)
                    .collection("Borrowed")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        int count = snapshot.size();
                        tvBorrowedCount.setText("Ödünç Alınan Kitap Sayısı: " + count);
                    });
        }

        btnHome.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, login.class));
            finish();
        });
    }
}
