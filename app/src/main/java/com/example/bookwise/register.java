package com.example.bookwise;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText etName, etSurname, etUsername, etEmail, etPassword, etBirthDate, etAddress;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.eTname);
        etSurname = findViewById(R.id.eTsurname);
        etUsername = findViewById(R.id.eTusername);
        etEmail = findViewById(R.id.eTmail);
        etPassword = findViewById(R.id.eTpassword);
        etBirthDate = findViewById(R.id.eTbirthdate);
        etAddress = findViewById(R.id.eTaddress);
        btnRegister = findViewById(R.id.btnregister);

        etBirthDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(register.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formatted = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etBirthDate.setText(formatted);
                    }, year, month, day);

            datePickerDialog.show();
        });

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "E-posta ve parola zorunlu", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            HashMap<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("surname", surname);
                            user.put("username", username);
                            user.put("email", email);
                            user.put("birthDate", birthDate);
                            user.put("address", address);
                            user.put("isAdmin", false); // 🔒 Varsayılan admin değil

                            // 🔥 Firestore'a kayıt
                            firestore.collection("Users")
                                    .document(uid)
                                    .set(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(register.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(() -> {
                                            finish(); // login ekranına dön
                                        }, 1500);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(register.this, "Veri Kaydı Başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                        } else {
                            Toast.makeText(register.this, "Kayıt Hatası: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}