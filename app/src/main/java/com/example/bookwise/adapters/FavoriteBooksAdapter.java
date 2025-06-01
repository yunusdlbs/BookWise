package com.example.bookwise.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookwise.R;
import com.example.bookwise.models.Book;
import com.example.bookwise.mybooks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteBooksAdapter extends RecyclerView.Adapter<FavoriteBooksAdapter.ViewHolder> {

    private Context context;
    private List<Book> favoriteList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();

    public FavoriteBooksAdapter(Context context, List<Book> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = favoriteList.get(position);

        // 🔁 Görünüm sıfırlama – view recycling hatasını engeller
        holder.btnBorrow.setVisibility(View.VISIBLE);
        holder.btnRemove.setVisibility(View.VISIBLE);
        holder.description.setVisibility(View.VISIBLE);
        holder.category.setVisibility(View.VISIBLE);
        holder.pageCount.setVisibility(View.VISIBLE);
        holder.stock.setVisibility(View.VISIBLE);

        // 🔁 Veriyi doldur
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.description.setText(book.getDescription());
        holder.category.setText("Kategori: " + (book.getCategory() != null ? book.getCategory() : "-"));
        holder.pageCount.setText("Sayfa: " + (book.getPageCount() != null ? book.getPageCount() : "-"));
        holder.stock.setText("Stok: " + book.getStock());

        Glide.with(context).load(book.getImageUrl()).into(holder.image);

        holder.btnBorrow.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String bookId = book.getTitle() + "_" + book.getAuthor();

            // 🔎 Önce kontrol: zaten ödünç alınmış mı?
            db.collection("Users").document(uid)
                    .collection("Borrowed")
                    .document(bookId)
                    .get()
                    .addOnSuccessListener(docSnapshot -> {
                        if (docSnapshot.exists()) {
                            Toast.makeText(context, "Bu kitabı zaten ödünç aldınız!", Toast.LENGTH_SHORT).show();
                        } else {
                            // 🟢 Ödünç alma işlemi başlat
                            Map<String, Object> data = new HashMap<>();
                            data.put("title", book.getTitle());
                            data.put("author", book.getAuthor());
                            data.put("imageUrl", book.getImageUrl());
                            data.put("borrowedAt", FieldValue.serverTimestamp());

                            db.collection("Users").document(uid)
                                    .collection("Borrowed")
                                    .document(bookId)
                                    .set(data)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Ödünç alındı", Toast.LENGTH_SHORT).show();

                                        // 🔽 Firestore’da stok düşür
                                        db.collection("books")
                                                .whereEqualTo("title", book.getTitle())
                                                .whereEqualTo("author", book.getAuthor())
                                                .get()
                                                .addOnSuccessListener(query -> {
                                                    if (!query.isEmpty()) {
                                                        for (DocumentSnapshot doc : query.getDocuments()) {
                                                            doc.getReference().update("stock", FieldValue.increment(-1));
                                                        }
                                                    }
                                                });
                                    });
                        }
                    });
        });



        holder.btnRemove.setOnClickListener(v -> {
            db.collection("Users").document(uid)
                    .collection("Favorites")
                    .document(book.getTitle() + "_" + book.getAuthor())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show();

                        // ✅ Aktiviteyi yenile (fragment tetiklenir)
                        Intent intent = new Intent(context, mybooks.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
        });

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, description, category, pageCount, stock;
        ImageView image;
        Button btnBorrow, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            description = itemView.findViewById(R.id.bookDescription);
            category = itemView.findViewById(R.id.bookCategory);
            pageCount = itemView.findViewById(R.id.bookPageCount);
            stock = itemView.findViewById(R.id.bookStock);
            image = itemView.findViewById(R.id.bookImage);
            btnBorrow = itemView.findViewById(R.id.btnBorrow);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
