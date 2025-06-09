package com.example.bookwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookwise.adapters.UserListAdapter;
import com.example.bookwise.models.UserInfo;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class edituser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private List<UserInfo> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button btnHome;
    private static final String TAG = "EditUser";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edituser);

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(this, userList);
        recyclerView.setAdapter(adapter);
        btnHome = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(edituser.this, home.class);
            startActivity(intent);
            Toast.makeText(this, "Ana Menüye Dönülüyor", Toast.LENGTH_SHORT).show();
        });

        loadUsers();
    }

    private void loadUsers() {
        db.collection("Users").get()
                .addOnSuccessListener(userDocs -> {
                    if (userDocs.isEmpty()) {
                        Log.w(TAG, "No users found in Users collection");
                        Toast.makeText(this, "Hiç kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    int totalUsers = userDocs.size();
                    Log.d(TAG, "Total users found: " + totalUsers);
                    AtomicInteger processedUsers = new AtomicInteger(0);

                    userList.clear();

                    for (DocumentSnapshot userDoc : userDocs) {
                        String uid = userDoc.getId();
                        String finalUsername = userDoc.getString("username") != null ? userDoc.getString("username") : "Bilinmeyen";

                        Log.d(TAG, "Processing user: uid=" + uid + ", username=" + finalUsername);

                        db.collection("Users")
                                .document(uid)
                                .collection("Borrowed")
                                .get()
                                .addOnSuccessListener(borrowedDocs -> {
                                    int borrowedCount = 0;
                                    boolean isGraylisted = false;

                                    if (borrowedDocs.isEmpty()) {
                                        Log.d(TAG, "No borrowed books found for user " + uid);
                                    } else {
                                        Log.d(TAG, "Borrowed docs for user " + uid + ": count=" + borrowedDocs.size());
                                        for (DocumentSnapshot doc : borrowedDocs) {
                                            try {
                                                Timestamp ts = doc.getTimestamp("borrowedAt");
                                                if (ts != null) {
                                                    long days = (System.currentTimeMillis() - ts.toDate().getTime()) / (1000 * 60 * 60 * 24);
                                                    Log.d(TAG, "Book " + doc.getId() + " borrowed " + days + " days ago");
                                                    borrowedCount++;
                                                    if (days > 15) {
                                                        isGraylisted = true;
                                                    }
                                                } else {
                                                    Log.w(TAG, "borrowedAt is null for book " + doc.getId() + " of user " + uid);
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "Invalid borrowedAt type for book " + doc.getId() + " of user " + uid + ": " + e.getMessage(), e);
                                            }
                                        }
                                    }

                                    UserInfo user = new UserInfo(uid, finalUsername, borrowedCount, isGraylisted);
                                    userList.add(user);
                                    Log.d(TAG, "Added user: username=" + finalUsername + ", borrowed=" + borrowedCount + ", graylisted=" + isGraylisted);

                                    if (processedUsers.incrementAndGet() == totalUsers) {
                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "All users processed, updating adapter. Total users: " + userList.size());
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to load borrowed books for user " + uid + ": " + e.getMessage(), e);
                                    if (e.getMessage().contains("PERMISSION_DENIED")) {
                                        Toast.makeText(this, "Erişim izni hatası: " + finalUsername + " kullanıcısının verileri alınamadı", Toast.LENGTH_LONG).show();
                                    }
                                    UserInfo user = new UserInfo(uid, finalUsername, 0, false);
                                    userList.add(user);
                                    Log.d(TAG, "Added user with error: username=" + finalUsername + ", borrowed=0 (due to error)");

                                    if (processedUsers.incrementAndGet() == totalUsers) {
                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "All users processed (with errors), updating adapter. Total users: " + userList.size());
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load users: " + e.getMessage(), e);
                    Toast.makeText(this, "Kullanıcılar yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();
                });
    }
}