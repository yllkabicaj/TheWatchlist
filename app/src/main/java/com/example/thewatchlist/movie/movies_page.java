package com.example.thewatchlist.movie;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thewatchlist.R;
import com.example.thewatchlist.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class movies_page extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton add_button;

    DatabaseHelper myDB;
    ArrayList<String> movie_id, movie_name, movie_year, movie_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movies_page);
       recyclerView = findViewById(R.id.recycler_view);
       add_button = findViewById(R.id.add_button);
       add_button.setOnClickListener(new View.OnClickListener(){

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
    }

    void displayMovies(){
        Cursor cursor = myDB.getAllMovies();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No movies to display.", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                movie_id.add(cursor.getString(0));
                movie_name.add(cursor.getString(1));
                movie_year.add(cursor.getString(2));
                movie_status.add(cursor.getString(3));

            }
        }
    }
}