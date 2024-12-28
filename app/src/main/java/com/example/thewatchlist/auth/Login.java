package com.example.thewatchlist.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerText = findViewById(R.id.register_text);
        TextView forgotPasswordText = findViewById(R.id.forgot_password_text);

        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(password);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM users WHERE email=? AND password=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, hashedPassword});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("_id"));

            String otp = generateOtp();
            EmailUtil.sendOtpEmail(email, otp);  // Send OTP via email

            long timestamp = System.currentTimeMillis();
            dbHelper.storeOtp(userId, otp, timestamp);

            Intent intent = new Intent(Login.this, OtpVerification.class);
            intent.putExtra("userId", userId);  // Pass userId to OTP screen
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login failed. Invalid email or password", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
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
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

}
