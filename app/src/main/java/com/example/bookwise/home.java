package com.example.bookwise;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.bookwise.adapters.BooksAdapter;
import com.example.bookwise.models.Book;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Book> bookList;
    private BooksAdapter adapter;
    private FirebaseFirestore db;
    private List<Book> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
// QueryHint garanti altına al
        searchView.setQueryHint("Kitap, Yazar veya Konu ara...");
// SearchView içindeki EditText'e eriş
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
// Görünürlük sabitle
        searchEditText.setHintTextColor(Color.parseColor("#808080")); // Gri
        searchEditText.setTextColor(Color.parseColor("#000000"));     // Siyah
        searchEditText.setBackgroundColor(Color.TRANSPARENT);         // Transparent arkaplan
        searchEditText.setCursorVisible(true);                        // İmleç açık
        searchEditText.setTextSize(16f);

        View headerView = navigationView.getHeaderView(0);
        TextView tvWelcome = headerView.findViewById(R.id.tvWelcome);

        if (user != null) {
            String email = user.getEmail();
            String uid = user.getUid();
            //tvWelcome.setText("Hoşgeldin\n" + email);

            firestore.collection("Users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                            if (Boolean.TRUE.equals(isAdmin)) {
                                // 🔥 Admin item'ını dinamik olarak ekle
                                String username = documentSnapshot.getString("username");
                                if (username != null && !username.isEmpty()) {
                                    tvWelcome.setText("Welcome\n" + username);
                                } else {
                                    tvWelcome.setText("Welcome\n" + email); // fallback
                                }
                                Menu menu = navigationView.getMenu();
                                SubMenu adminSubMenu = menu.addSubMenu("Admin");
                                adminSubMenu.setGroupCheckable(R.id.group_main, true, true);

// Alt itemlar:
                                adminSubMenu.add("Kullanıcı Düzenle").setIcon(R.drawable.ic_users).setOnMenuItemClickListener(item1 -> {
                                    startActivity(new Intent(this, edituser.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                });

                                adminSubMenu.add("Kitap Ekle").setIcon(R.drawable.ic_add_book).setOnMenuItemClickListener(item2 -> {
                                    startActivity(new Intent(this, addbook.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                });

                                adminSubMenu.add("Kitap Sil").setIcon(R.drawable.ic_delete_book).setOnMenuItemClickListener(item3 -> {
                                    startActivity(new Intent(this, deletebook.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                });

                                adminSubMenu.add("Kitap Düzenle").setIcon(R.drawable.ic_edit_book).setOnMenuItemClickListener(item4 -> {
                                    startActivity(new Intent(this, editbook.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                });
                                if (menu.findItem(R.id.nav_admin) == null) {
                                    menu.add(R.id.group_main, R.id.nav_admin, Menu.NONE, "Admin")
                                            .setIcon(R.drawable.ic_admin_panel);
                                }
                            }
                        }
                    });

            firestore.collection("books")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        bookList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Book book = doc.toObject(Book.class);
                            bookList.add(book);
                        }
                        adapter = new BooksAdapter(this, bookList);
                        recyclerView.setAdapter(adapter);
                    });

        }

// TOOLBAR ve TOGGLE
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        toggle.syncState();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

// MENÜ ITEM TIKLAMALARI
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    Toast.makeText(home.this, "Profil açılıyor...", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_settings) {
                    Toast.makeText(home.this, "Ayarlar geliyor...", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(home.this, login.class));
                    finish();
                }
//                else if (id == R.id.nav_admin) {
//                    startActivity(new Intent(home.this, admin.class));
//                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        adapter = new BooksAdapter(this, bookList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("books").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Book book = doc.toObject(Book.class);
                        bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Kitaplar yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks() {
        db.collection("books").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Book book = doc.toObject(Book.class);
                        bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Kitaplar yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterBooks(String text) {
        filteredList.clear();
        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(text.toLowerCase()) ||
                    book.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(book);
            }
        }
        adapter.updateList(filteredList);
    }
}