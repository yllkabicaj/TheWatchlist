package com.example.thewatchlist.auth;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "ForgotPassword";

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

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send Reset Email button clicked");
                try {
                    sendResetEmail();
                } catch (Exception e) {
                    Log.e(TAG, "Error in sendResetEmail", e);
                    Toast.makeText(ForgotPassword.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    resetPassword();
                } catch (Exception e) {
                    Toast.makeText(ForgotPassword.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendResetEmail() throws Exception {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM users WHERE email=?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                String resetCode = generateResetCode();
                ContentValues cv = new ContentValues();

                db.update("users", cv, "email=?", new String[]{email});

                String subject = "Password Reset Request";
                String body = "Use the following code to reset your password: " + resetCode;
                EmailUtil.sendEmail(email, subject, body);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        codeEditText.setVisibility(View.VISIBLE);
                        newPasswordEditText.setVisibility(View.VISIBLE);
                        resetPasswordButton.setVisibility(View.VISIBLE);
                        sendEmailButton.setVisibility(View.GONE);
                    }
                });

                Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Email address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private void resetPassword() throws Exception {
        String email = emailEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getWritableDatabase();
            String query = "SELECT * FROM users WHERE email=? AND reset_code=?";
            cursor = db.rawQuery(query, new String[]{email, code});

            if (cursor.getCount() > 0) {
                Log.d(TAG, "Valid reset code");
                ContentValues cv = new ContentValues();
                cv.put("password", hashPassword(newPassword));
                cv.put("reset_code", (String) null);

                int rowsUpdated = db.update("users", cv, "email=?", new String[]{email});

                Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Invalid email or reset code", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private String generateResetCode() {
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
