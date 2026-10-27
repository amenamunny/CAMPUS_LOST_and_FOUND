package com.cstu.campuslostfound;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail;
    private EditText etLoginPassword;

    private Button btnLogin;
    private TextView tvGoToRegister;
    private TextView tvForgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Initialize views
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Login button
        btnLogin.setOnClickListener(v -> {

            String email = etLoginEmail.getText()
                    .toString()
                    .trim();

            String password = etLoginPassword.getText()
                    .toString()
                    .trim();

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                        LoginActivity.this,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mAuth.signInWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    Toast.makeText(
                            LoginActivity.this,
                            "Login successful!",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );

                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(
                            LoginActivity.this,
                            "Login failed. Please check your email and password.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        // Forgot Password button
        tvForgotPassword.setOnClickListener(v -> {
            sendPasswordResetEmail();
        });

        // Open Register screen
        tvGoToRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    // Send password reset email using Firebase
    private void sendPasswordResetEmail() {

        String email = etLoginEmail.getText()
                .toString()
                .trim();

        // Check whether email is entered
        if (email.isEmpty()) {

            etLoginEmail.setError(
                    "Please enter your registered email"
            );

            etLoginEmail.requestFocus();

            Toast.makeText(
                    LoginActivity.this,
                    "Enter your email first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Request password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Password Reset")
                                .setMessage(
                                        "If an account exists for this email, " +
                                                "a password reset link will be sent.\n\n" +
                                                "Please check your inbox and spam folder."
                                )
                                .setPositiveButton("OK", null)
                                .show();

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to send reset email. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
