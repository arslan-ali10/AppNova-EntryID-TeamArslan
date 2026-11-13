
package com.example.campusfind_teamarslan;

import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPassword,
            etConfirmPassword;

    Button btnRegister;
    TextView tvLogin;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword =
                findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        databaseHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> register());

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void register() {

        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password =
                etPassword.getText().toString();

        String confirmPassword =
                etConfirmPassword.getText().toString();

        // Full Name validation
        if (!name.matches("[\\p{L} ]{3,}")) {
            etFullName.setError(
                    "Enter at least 3 letters"
            );
            return;
        }

        // Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            return;
        }

        // Password length
        if (password.length() < 6) {
            etPassword.setError(
                    "Password must contain 6 characters"
            );
            return;
        }

        // Password must contain a digit
        if (!password.matches(".*\\d.*")) {
            etPassword.setError(
                    "Password must contain at least one digit"
            );
            return;
        }

        // Confirm Password
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Passwords do not match"
            );
            return;
        }

        // Save user in database
        boolean inserted = databaseHelper.registerUser(
                name, email, password
        );

        if (inserted) {

            Toast.makeText(
                    this,
                    "Account created successfully!",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            etEmail.setError(
                    "An account with this email already exists"
            );
        }
    }
}
