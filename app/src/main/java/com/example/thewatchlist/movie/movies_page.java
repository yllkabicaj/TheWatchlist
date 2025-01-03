package com.example.thewatchlist.movie;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thewatchlist.CustomAdapter;
import com.example.thewatchlist.R;
import com.example.thewatchlist.auth.UserSessionManager;
import com.example.thewatchlist.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class movies_page extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton add_button;
    DatabaseHelper myDB;
    ArrayList<String> movie_id, movie_name, movie_year, movie_status;
    CustomAdapter customAdapter;
    ImageView noMoviesImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movies_page);

        recyclerView = findViewById(R.id.recycler_view);
        add_button = findViewById(R.id.add_button);
        noMoviesImage = findViewById(R.id.no_movies_image);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(movies_page.this, AddMovie.class);
                startActivity(intent);
            }
        });

        myDB = new DatabaseHelper(movies_page.this);

        movie_id = new ArrayList<>();
        movie_name = new ArrayList<>();
        movie_year = new ArrayList<>();
        movie_status = new ArrayList<>();

        displayMovies();

        customAdapter = new CustomAdapter(movies_page.this, this, movie_id, movie_name, movie_year, movie_status);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(movies_page.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void displayMovies() {
        movie_id.clear();
        movie_name.clear();
        movie_year.clear();
        movie_status.clear();

        UserSessionManager sessionManager = new UserSessionManager(this);
        int userId = sessionManager.getUserId();

        Cursor cursor = myDB.getAllMovies(userId);
        if (cursor.getCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noMoviesImage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMoviesImage.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                movie_id.add(cursor.getString(0));
                movie_name.add(cursor.getString(1));
                movie_year.add(cursor.getString(2));
                movie_status.add(cursor.getString(3));
            }
        }

        cursor.close();
    }
}
