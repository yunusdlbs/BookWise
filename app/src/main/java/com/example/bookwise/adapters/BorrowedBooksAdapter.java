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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BorrowedBooksAdapter extends RecyclerView.Adapter<BorrowedBooksAdapter.ViewHolder> {

    private Context context;
    private List<Book> borrowedList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public BorrowedBooksAdapter(Context context, List<Book> borrowedList) {
        this.context = context;
        this.borrowedList = borrowedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrowed_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = borrowedList.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        Glide.with(context).load(book.getImageUrl()).into(holder.image);

        if (book.getBorrowedAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(book.getBorrowedAt());
            calendar.add(Calendar.DAY_OF_YEAR, 15);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            holder.dueDate.setText("Son İade Tarihi: " + sdf.format(calendar.getTime()));
        } else {
            holder.dueDate.setText("İade tarihi alınamadı");
        }

        holder.btnReturn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String bookId = book.getTitle() + "_" + book.getAuthor();

            // 1️⃣ Kullanıcının Borrowed listesinden sil
            db.collection("Users").document(uid)
                    .collection("Borrowed")
                    .document(bookId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Kitap iade edildi", Toast.LENGTH_SHORT).show();

                        // 2️⃣ books koleksiyonundaki stok değerini artır
                        db.collection("books")
                                .whereEqualTo("title", book.getTitle())
                                .whereEqualTo("author", book.getAuthor())
                                .get()
                                .addOnSuccessListener(query -> {
                                    if (!query.isEmpty()) {
                                        for (DocumentSnapshot doc : query.getDocuments()) {
                                            doc.getReference().update("stock", FieldValue.increment(+1));
                                        }
                                    }
                                });

                        // 3️⃣ Adapter listesinden kaldır
                        Intent intent = new Intent(context, mybooks.class);
                        intent.putExtra("selected_tab", 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "İade işlemi başarısız", Toast.LENGTH_SHORT).show());
        });

    }

    @Override
    public int getItemCount() {
        return borrowedList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, dueDate;
        ImageView image;
        Button btnReturn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            image = itemView.findViewById(R.id.bookImage);
            dueDate = itemView.findViewById(R.id.tvDueDate);
            btnReturn = itemView.findViewById(R.id.btnReturn);
        }
    }
}
