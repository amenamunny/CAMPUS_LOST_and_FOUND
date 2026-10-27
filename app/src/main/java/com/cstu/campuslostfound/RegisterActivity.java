package com.cstu.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterName;
    private EditText etRegisterEmail;
    private EditText etRegisterPassword;

    private Button btnRegister;
    private TextView tvGoToLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> {

            String name = etRegisterName.getText()
                    .toString()
                    .trim();

            String email = etRegisterEmail.getText()
                    .toString()
                    .trim();

            String password = etRegisterPassword.getText()
                    .toString()
                    .trim();

            if (name.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (password.length() < 6) {

                Toast.makeText(
                        RegisterActivity.this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Create Firebase Authentication account
            mAuth.createUserWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    String userId = mAuth.getCurrentUser()
                            .getUid();

                    // Create user profile data
                    Map<String, Object> user = new HashMap<>();

                    user.put("name", name);
                    user.put("email", email);
                    user.put("department", "Not specified");

                    // Save profile to Firestore
                    db.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registration successful!",
                                        Toast.LENGTH_LONG
                                ).show();

                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        MainActivity.class
                                );

                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Account created, but profile could not be saved: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });

                } else {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration failed: "
                                    + task.getException()
                                    .getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        tvGoToLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}