package com.example.thewatchlist.auth;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thewatchlist.R;
import com.example.thewatchlist.database.DatabaseHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Registration extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, emailEditText;
    private DatabaseHelper dbHelper;
    private TextView loginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);
        loginText = findViewById(R.id.login_text);
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, Login.class);
            startActivity(intent);
        });
        Button registerButton = findViewById(R.id.register_button);

        dbHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters long and include an uppercase letter, " +
                    "a lowercase letter, a digit, and a special character", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String otp = generateOtp();
        long timestamp = System.currentTimeMillis();

        cv.put("username", username);
        cv.put("password", hashPassword(password));
        cv.put("email", email);
        cv.put("otp", otp);
        cv.put("otp_timestamp", timestamp);

        long result = db.insert("users", null, cv);

        if (result == -1) {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration successful! OTP sent to email.", Toast.LENGTH_SHORT).show();

            EmailUtil.sendEmail(email, "OTP Verification", "Your OTP is: " + otp);

            Intent intent = new Intent(Registration.this, OtpVerification.class);
            intent.putExtra("userId", (int) result);
            startActivity(intent);
            finish();
        }

        db.close();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }
}
