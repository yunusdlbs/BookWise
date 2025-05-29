package com.example.bookwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwise.R;
import com.example.bookwise.adapters.BorrowedBooksAdapter;
import com.example.bookwise.models.Book;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OduncKitaplarFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowedBooksAdapter adapter;
    private List<Book> bookList = new ArrayList<>();

    public OduncKitaplarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_odunc_kitaplar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerBorrowed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BorrowedBooksAdapter(getContext(), bookList);
        recyclerView.setAdapter(adapter);

        loadBorrowedBooks(); // burada çağır
    }
    private void loadBorrowedBooks() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .collection("Borrowed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Book book = doc.toObject(Book.class);

                        // 📅 İade tarihi için timestamp kontrolü
                        com.google.firebase.Timestamp ts = doc.getTimestamp("borrowedAt");
                        if (ts != null) {
                            book.setBorrowedAt(ts.toDate());
                        }

                        bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Ödünç kitaplar yüklenemedi", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBorrowedBooks();
    }
}
