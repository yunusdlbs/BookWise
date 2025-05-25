package com.example.bookwise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class addbook extends AppCompatActivity {

    private EditText etTitle, etAuthor, etDescription, etPageCount, etStock, etCategory, etImageUrl;
    private Button btnAddBook;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbook);

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etDescription = findViewById(R.id.etDescription);
        etPageCount = findViewById(R.id.etPageCount);
        etStock = findViewById(R.id.etStock);
        etCategory = findViewById(R.id.etCategory);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnAddBook = findViewById(R.id.btnAddBook);

        firestore = FirebaseFirestore.getInstance();

        btnAddBook.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Oturum açılmadı! Lütfen giriş yapın.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, login.class));
                finish();
                return;
            }

            firestore.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && Boolean.TRUE.equals(documentSnapshot.getBoolean("isAdmin"))) {
                            saveBookToFirestore();
                        } else {
                            Toast.makeText(this, "Bu işlem için admin yetkisi gerekiyor!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Admin kontrol hatası: ", e);
                        Toast.makeText(this, "Admin yetkisi kontrol edilemedi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void saveBookToFirestore() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String pageCountStr = etPageCount.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Giriş doğrulama
        if (title.isEmpty() || author.isEmpty() || description.isEmpty() || category.isEmpty() || pageCountStr.isEmpty() || stockStr.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        // URL doğrulama (sadece http veya https ile başlasın)
        Log.d("UrlDebug", "Kontrol edilen URL: " + imageUrl);
        if (!isValidUrl(imageUrl)) {
            Log.d("UrlDebug", "URL geçersiz kabul edildi.");
            Toast.makeText(this, "Geçerli bir URL giriniz (http:// veya https:// ile başlamalı)!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("UrlDebug", "URL geçerli kabul edildi.");

        //int pageCount, stock;
        int stock;
        try {
            //pageCount = Integer.parseInt(pageCountStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Sayfa sayısı ve stok geçerli bir sayı olmalı!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> book = new HashMap<>();
        book.put("title", title);
        book.put("author", author);
        book.put("description", description);
        book.put("category", category);
        book.put("pageCount", pageCountStr);
        book.put("stock", stock);
        book.put("imageUrl", imageUrl);

        firestore.collection("books")
                .add(book)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Kitap başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Kitap ekleme hatası: ", e);
                    Toast.makeText(this, "Kayıt başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Güncellenmiş URL doğrulama yöntemi (sadece http veya https ile başlasın)
    private boolean isValidUrl(String url) {
        String urlPattern = "^https?://";
        Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        boolean matches = pattern.matcher(url).find(); // find() ile başlama kontrolü
        Log.d("UrlDebug", "Regex eşleşti mi? " + matches);
        return matches;
    }
}