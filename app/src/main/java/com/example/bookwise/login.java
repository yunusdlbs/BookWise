package com.example.bookwise;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.editTextText); // veya senin kullandığın id'ye göre
        etPassword = findViewById(R.id.editTextTextPassword);
        Button btnRegister = findViewById(R.id.register);
        Button btnLogin = findViewById(R.id.login);
        Button btnAboutUs = findViewById(R.id.aboutus);
        TextView tvCountdown = findViewById(R.id.tvCountdown);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            tvCountdown.setVisibility(View.VISIBLE);
                            new CountDownTimer(5000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    long saniye = millisUntilFinished / 1000;
                                    tvCountdown.setText("Lütfen Bekleyiniz: " + saniye + " sn");
                                    Log Log = null;
                                    Log.d("DEBUG", "Firebase giriş başarılı oldu!");
                                }
                                public void onFinish() {
                                    //tvCountdown.setText("Logged In");
                                    startActivity(new Intent(login.this, home.class));
                                    finish();
                                }
                            }.start();

                            //Toast.makeText(this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();

                        } else {
                            //Toast.makeText(this, "Giriş Başarısız, Bilgilerinizi tekrar kontrol ediniz! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "Giriş Başarısız, Bilgilerinizi tekrar kontrol ediniz! ", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, register.class);
            startActivity(intent);
        });

        btnAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, aboutus.class);
            startActivity(intent);
        });

    }
}