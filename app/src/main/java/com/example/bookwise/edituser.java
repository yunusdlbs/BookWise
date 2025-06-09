package com.example.bookwise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
    private Button btnhome;
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
        btnhome = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        loadUsers();

        btnhome.setOnClickListener(v -> {
            Intent intent = new Intent(edituser.this, home.class);
            startActivity(intent);
        });
    }



    private void loadUsers() {
        db.collection("Users").get()
                .addOnSuccessListener(userDocs -> {
                    if (userDocs.isEmpty()) {
                        Log.w(TAG, "No users found in Users collection");
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    int totalUsers = userDocs.size();
                    AtomicInteger processedUsers = new AtomicInteger(0);

                    userList.clear();

                    for (DocumentSnapshot userDoc : userDocs) {
                        String uid = userDoc.getId();
                        // username'i final olarak tanımla
                        final String finalUsername = userDoc.getString("username") != null ? userDoc.getString("username") : "Bilinmeyen Kullanıcı";

                        Log.d(TAG, "Processing user: " + uid + ", Username: " + finalUsername);

                        // Borrowed koleksiyonunu çek
                        db.collection("Users")
                                .document(uid)
                                .collection("Borrowed")
                                .get()
                                .addOnSuccessListener(borrowedDocs -> {
                                    int borrowedCount = borrowedDocs.size();
                                    boolean isGraylisted = false;

                                    for (DocumentSnapshot doc : borrowedDocs) {
                                        Timestamp ts = doc.getTimestamp("borrowedAt");
                                        if (ts != null) {
                                            long days = (System.currentTimeMillis() - ts.toDate().getTime()) / (1000 * 60 * 60 * 24);
                                            if (days > 15) {
                                                isGraylisted = true;
                                                break;
                                            }
                                        }
                                    }

                                    UserInfo user = new UserInfo(uid, finalUsername, borrowedCount, isGraylisted);
                                    userList.add(user);
                                    Log.d(TAG, "Added user: " + finalUsername + ", Borrowed: " + borrowedCount + ", Graylisted: " + isGraylisted);

                                    if (processedUsers.incrementAndGet() == totalUsers) {
                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "All users processed, updating adapter. Total users: " + userList.size());
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to load borrowed books for user " + uid, e);
                                    // Hata durumunda bile kullanıcıyı ekle (varsayılan değerlerle)
                                    UserInfo user = new UserInfo(uid, finalUsername, 0, false);
                                    userList.add(user);

                                    if (processedUsers.incrementAndGet() == totalUsers) {
                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "All users processed (with errors), updating adapter. Total users: " + userList.size());
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load users", e);
                    adapter.notifyDataSetChanged();
                });
    }
}