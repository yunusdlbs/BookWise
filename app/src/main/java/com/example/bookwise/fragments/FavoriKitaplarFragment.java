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
import com.example.bookwise.adapters.FavoriteBooksAdapter;
import com.example.bookwise.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoriKitaplarFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteBooksAdapter adapter;
    private List<Book> favoriteList = new ArrayList<>();
    private FirebaseFirestore db;
    private String uid;

    public FavoriKitaplarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favori_kitaplar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new FavoriteBooksAdapter(getContext(), favoriteList);
        recyclerView.setAdapter(adapter);

        listenFavoritesRealtime();
    }

    private void listenFavoritesRealtime() {
        db.collection("Users")
                .document(uid)
                .collection("Favorites")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Favoriler yüklenemedi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        List<Book> updatedList = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Book book = doc.toObject(Book.class);
                            updatedList.add(book);
                        }

                        favoriteList.clear();
                        favoriteList.addAll(updatedList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        listenFavoritesRealtime(); // fragment görünür olduğunda listeyi yenile
    }
}
