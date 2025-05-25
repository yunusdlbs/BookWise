package com.example.bookwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class editbook extends AppCompatActivity {

    private EditText etBookTitle, etAuthorName, etNewStock;
    private Button btnUpdateStock, btnBack;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editbook);

        etBookTitle = findViewById(R.id.etBookTitle);
        etAuthorName = findViewById(R.id.etAuthorName);
        etNewStock = findViewById(R.id.etNewStock);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);
        btnBack = findViewById(R.id.btnBack);
        firestore = FirebaseFirestore.getInstance();

        btnUpdateStock.setOnClickListener(v -> {
            String title = etBookTitle.getText().toString().trim();
            String author = etAuthorName.getText().toString().trim();
            String stockStr = etNewStock.getText().toString().trim();

            if (title.isEmpty() || author.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show();
                return;
            }

            int newStock;
            try {
                newStock = Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Geçerli bir sayı girin", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("books")
                    .whereEqualTo("title", title)
                    .whereEqualTo("author", author)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                doc.getReference().update("stock", newStock)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Stok başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                            etBookTitle.setText("");
                                            etAuthorName.setText("");
                                            etNewStock.setText("");
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(this, "Kitap bulunamadı, bilgileri kontrol edin", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(editbook.this, home.class);
            startActivity(intent);
        });
    }
}