package com.example.bookwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class deletebook extends AppCompatActivity {

    private EditText etBookTitle, etAuthorName;
    private Button btnDeleteBook, btnBack;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deletebook);

        etBookTitle = findViewById(R.id.etBookTitle);
        etAuthorName = findViewById(R.id.etAuthorName);
        btnDeleteBook = findViewById(R.id.btnDeleteBook);
        btnBack = findViewById(R.id.btnBack);
        firestore = FirebaseFirestore.getInstance();

        btnDeleteBook.setOnClickListener(v -> {
            String title = etBookTitle.getText().toString().trim();
            String author = etAuthorName.getText().toString().trim();

            if (title.isEmpty() || author.isEmpty()) {
                Toast.makeText(this, "Lütfen kitap ve yazar adını giriniz", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore'dan silinecek belgeyi bul
            firestore.collection("books")
                    .whereEqualTo("title", title)
                    .whereEqualTo("author", author)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                doc.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Kitap başarıyla silindi", Toast.LENGTH_SHORT).show();
                                            etBookTitle.setText("");
                                            etAuthorName.setText("");
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Silme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(this, "Kitap bulunamadı, bilgileri kontrol edin", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(deletebook.this, home.class);
            startActivity(intent);
        });
    }
}