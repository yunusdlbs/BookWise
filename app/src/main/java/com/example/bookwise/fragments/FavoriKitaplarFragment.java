package com.example.bookwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    private TextView tvEmptyMessage;
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
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteBooksAdapter(getContext(), favoriteList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listenFavoritesRealtime();
    }

    private void listenFavoritesRealtime() {
        db.collection("Users")
                .document(uid)
                .collection("Favorites")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        //Toast.makeText(getContext(), "Favoriler yüklenemedi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        favoriteList.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Book book = doc.toObject(Book.class);
                            favoriteList.add(book);
                        }
                        adapter.notifyDataSetChanged();
                        tvEmptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        favoriteList.clear();
                        adapter.notifyDataSetChanged();
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        listenFavoritesRealtime(); // fragment görünür olduğunda listeyi yenile
    }
}
