package com.example.thewatchlist.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.thewatchlist.movie.movies_page;

public class OtpVerification extends AppCompatActivity {

    private EditText otpEditText;
    private int userId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpEditText = findViewById(R.id.otp_input);
        Button verifyButton = findViewById(R.id.verify_otp_button);

        userId = getIntent().getIntExtra("userId", -1);
        dbHelper = new DatabaseHelper(this);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }

    private void verifyOtp() {
        String enteredOtp = otpEditText.getText().toString();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT otp, otp_timestamp FROM users WHERE _id=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String storedOtp = cursor.getString(cursor.getColumnIndex("otp"));
            @SuppressLint("Range") long timestamp = cursor.getLong(cursor.getColumnIndex("otp_timestamp"));

            long currentTime = System.currentTimeMillis();
            if (storedOtp.equals(enteredOtp) && (currentTime - timestamp) < 300000) {
                Toast.makeText(this, "Code verified. Login successful!", Toast.LENGTH_SHORT).show();

                UserSessionManager sessionManager = new UserSessionManager(this);
                sessionManager.setUserId(userId);

                Intent intent = new Intent(OtpVerification.this, movies_page.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid or expired verification code", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }
}
