package com.example.bookwise.adapters;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<Book> bookList;
    private Context context;

    // Genişleme durumu kontrolü için
    private SparseBooleanArray expandedItems = new SparseBooleanArray();

    public BooksAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }
    //  extend etme süreicini burda tamamladık,
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.description.setText(book.getDescription());
        Glide.with(context).load(book.getImageUrl()).into(holder.image);

        // Yeni alanlar
        holder.category.setText("Kategori: " + book.getCategory());
        holder.pageCount.setText("Sayfa Sayısı: " + book.getPageCount());
        holder.stock.setText("Stok: " + book.getStock());

        // Ödünç Al butonu
        if (book.getStock() > 0) {
            holder.btnBorrow.setEnabled(true);
            holder.btnBorrow.setAlpha(1f);
            holder.warning.setVisibility(View.GONE);
        } else {
            holder.btnBorrow.setEnabled(false);
            holder.btnBorrow.setAlpha(0.5f);
            holder.warning.setVisibility(View.VISIBLE);
        }

        holder.btnBorrow.setOnClickListener(v -> {
            Toast.makeText(context, "Ödünç alındı: " + book.getTitle(), Toast.LENGTH_SHORT).show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("title", book.getTitle());
                data.put("author", book.getAuthor());
                data.put("imageUrl", book.getImageUrl());

                db.collection("Users").document(uid)
                        .collection("Borrowed")
                        .document(book.getTitle() + "_" + book.getAuthor())
                        .set(data)
                        .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Favori eklendi"))
                        .addOnFailureListener(e -> Log.e("FIREBASE", "HATA: " + e.getMessage()));
            }
        });

        holder.btnFavorite.setOnClickListener(v -> {
            Toast.makeText(context, book.getTitle() + " favorilere eklendi!", Toast.LENGTH_SHORT).show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("title", book.getTitle());
                data.put("author", book.getAuthor());
                data.put("imageUrl", book.getImageUrl());

                db.collection("Users").document(uid)
                        .collection("Favorites")
                        .document(book.getTitle() + "_" + book.getAuthor())
                        .set(data);
            }
        });

        // Kart tıklanınca detayları göster/gizle
        boolean isExpanded = expandedItems.get(position, false);
        holder.detailLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            expandedItems.put(position, !isExpanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateList(List<Book> newList) {
        bookList = newList;
        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, description, category, pageCount, stock;
        ImageView image;
        View detailLayout;
        Button btnBorrow, btnFavorite;

        TextView warning;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            warning = itemView.findViewById(R.id.tvOutOfStockWarning);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            description = itemView.findViewById(R.id.bookDescription);
            image = itemView.findViewById(R.id.bookImage);

            category = itemView.findViewById(R.id.bookCategory);
            pageCount = itemView.findViewById(R.id.bookPageCount);
            stock = itemView.findViewById(R.id.bookStock);
            detailLayout = itemView.findViewById(R.id.detailLayout);
            btnBorrow = itemView.findViewById(R.id.btnBorrow);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

        }
    }
}
