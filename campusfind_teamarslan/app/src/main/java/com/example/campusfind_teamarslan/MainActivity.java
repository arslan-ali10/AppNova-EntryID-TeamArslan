
package com.example.campusfind_teamarslan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        databaseHelper = new DatabaseHelper(this);

        // Login Button
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                return;
            }

            // Check email and password in SQLite
            if (databaseHelper.checkUser(email, password)) {

                Toast.makeText(
                        MainActivity.this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                ).show();

                // Open Dashboard
                Intent intent = new Intent(
                        MainActivity.this,
                        DashboardActivity.class
                );

                intent.putExtra("USER_EMAIL", email);

                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Open Register Screen
        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}
