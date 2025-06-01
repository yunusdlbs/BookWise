package com.example.bookwise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class addbook extends AppCompatActivity {

    private EditText etTitle, etAuthor, etDescription, etPageCount, etStock, etCategory;
    private Button btnAddBook;
    private FirebaseFirestore firestore;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView ivBookImage;
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    //private FirebaseFirestore firestore;

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
        //etImageUrl = findViewById(R.id.etImageUrl);
        btnAddBook = findViewById(R.id.btnAddBook);

        firestore = FirebaseFirestore.getInstance();

        ivBookImage = findViewById(R.id.ivBookImage);
        progressDialog = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ivBookImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

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

                            // Formdan verileri çek
                            String title = etTitle.getText().toString().trim();
                            String author = etAuthor.getText().toString().trim();
                            String description = etDescription.getText().toString().trim();
                            String category = etCategory.getText().toString().trim();
                            String pageCount = etPageCount.getText().toString().trim();
                            String stockStr = etStock.getText().toString().trim();

                            if (title.isEmpty() || author.isEmpty() || description.isEmpty() || category.isEmpty() || pageCount.isEmpty() || stockStr.isEmpty()) {
                                Toast.makeText(this, "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int stock;
                            try {
                                stock = Integer.parseInt(stockStr);
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Stok geçerli bir sayı olmalı!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 🔥 Yeni metot
                            uploadBookWithImage(title, author, description, category, stock, pageCount);

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

    private void uploadBookWithImage(String title, String author, String description, String category, int stock, String pageCount) {
        if (imageUri == null) {
            Toast.makeText(this, "Lütfen kitap görseli seçin!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Yükleniyor...");
        progressDialog.show();

        String fileName = title + "_" + System.currentTimeMillis();
        storage.getReference()
                .child("book_images/" + fileName)
                .putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Kitap objesi
                        Map<String, Object> book = new HashMap<>();
                        book.put("title", title);
                        book.put("author", author);
                        book.put("description", description);
                        book.put("category", category);
                        book.put("pageCount", pageCount);
                        book.put("stock", stock);
                        book.put("imageUrl", imageUrl); // 🔥 Storage'dan gelen URL

                        firestore.collection("books")
                                .add(book)
                                .addOnSuccessListener(documentReference -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "Kitap başarıyla eklendi", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "Kayıt başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Görsel yükleme başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivBookImage.setImageURI(imageUri); // Seçilen resmi göster
        }
    }


}