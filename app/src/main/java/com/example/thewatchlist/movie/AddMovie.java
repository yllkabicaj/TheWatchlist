package com.example.thewatchlist.movie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thewatchlist.auth.UserSessionManager;
import com.example.thewatchlist.database.DatabaseHelper;
import com.example.thewatchlist.R;
import com.example.thewatchlist.enums.Status;

public class AddMovie extends AppCompatActivity {

    EditText edit_text_year, edit_text_name;
    Button add_movie_button;
    Spinner status_spinner;
    UserSessionManager sessionManager;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_movie);

        sessionManager = new UserSessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == -1) {
            Log.e("AddMovie", "User ID is invalid.");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_year = findViewById(R.id.edit_text_year);
        status_spinner = findViewById(R.id.status_spinner);
        add_movie_button = findViewById(R.id.add_movie_button);

        // Populate the spinner with enums from Status
        String[] statusArray = new String[Status.values().length];
        for (int i = 0; i < Status.values().length; i++) {
            statusArray[i] = Status.values()[i].name();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(adapter);

        add_movie_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = edit_text_name.getText().toString().trim();
                String movieYearString = edit_text_year.getText().toString().trim();
                String movieStatus = status_spinner.getSelectedItem().toString();

                if (movieTitle.isEmpty() || movieYearString.isEmpty() || movieStatus.isEmpty()) {
                    Toast.makeText(AddMovie.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int movieYear;
                try {
                    movieYear = Integer.parseInt(movieYearString);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddMovie.this, "Please enter a valid year", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(AddMovie.this);
                dbHelper.addMovie(movieTitle, movieYear, Status.valueOf(movieStatus), userId);
                Toast.makeText(AddMovie.this, "Movie added successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AddMovie.this, movies_page.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
