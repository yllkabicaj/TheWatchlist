package com.example.thewatchlist.auth;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thewatchlist.R;
import com.example.thewatchlist.database.DatabaseHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText, codeEditText, newPasswordEditText;
    private Button sendEmailButton, resetPasswordButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email);
        codeEditText = findViewById(R.id.code);
        newPasswordEditText = findViewById(R.id.new_password);
        sendEmailButton = findViewById(R.id.send_email_button);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        dbHelper = new DatabaseHelper(this);

        sendEmailButton.setOnClickListener(v -> {
            try {
                sendResetEmail();
            } catch (Exception e) {
                Toast.makeText(ForgotPassword.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        resetPasswordButton.setOnClickListener(v -> {
            try {
                resetPassword();
            } catch (Exception e) {
                Toast.makeText(ForgotPassword.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendResetEmail() throws Exception {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});

        if (cursor.getCount() > 0) {
            String otp = generateOtp();
            String subject = "Password Reset Request";
            String body = "Use the following OTP to reset your password: " + otp;

            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put("otp", otp);
            db.update("users", contentValues, "email=?", new String[]{email});

            EmailUtil.sendEmail(email, subject, body);

            runOnUiThread(() -> {
                codeEditText.setVisibility(View.VISIBLE);
                newPasswordEditText.setVisibility(View.VISIBLE);
                resetPasswordButton.setVisibility(View.VISIBLE);
                sendEmailButton.setVisibility(View.GONE);
            });

            Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Email address not found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    private void resetPassword() throws Exception {
        String email = emailEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND otp=?", new String[]{email, code});

        if (cursor.getCount() > 0) {
           ContentValues contentValues = new ContentValues();
            contentValues.put("password", hashPassword(newPassword));
            contentValues.put("otp", (String) null);  // Clear the OTP field after reset
            db.update("users", contentValues, "email=?", new String[]{email});

            Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Invalid email or OTP", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
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
}
