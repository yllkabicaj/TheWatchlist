package com.example.thewatchlist.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thewatchlist.R;
import com.example.thewatchlist.database.DatabaseHelper;
import com.example.thewatchlist.enums.Status;

public class UpdateMovie extends AppCompatActivity {
    EditText title_input, year_input;
    Spinner status_input;
    Button edit_button;
    String id, title, year, status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);
        title_input = findViewById(R.id.edit_text_name1);
        year_input = findViewById(R.id.edit_text_year1);
        status_input = findViewById(R.id.status_spinner1);
        edit_button = findViewById(R.id.edit_movie_button);
        populateSpinner();
        getIntentAndSetMovie();

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_input.getText().toString().trim();
                year = year_input.getText().toString().trim();
                status = status_input.getSelectedItem().toString();

                if (title.isEmpty() || year.isEmpty() || status.isEmpty()) {
                    Toast.makeText(UpdateMovie.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper myDB = new DatabaseHelper(UpdateMovie.this);
                myDB.updateMovie(id, title, year, status);

                // Navigate back to movies_page
                Intent intent = new Intent(UpdateMovie.this, movies_page.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void getIntentAndSetMovie() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("year") && getIntent().hasExtra("status")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            year = getIntent().getStringExtra("year");
            status = getIntent().getStringExtra("status");

            title_input.setText(title);
            year_input.setText(year);
            setSpinnerValue(status_input, status);
        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateSpinner() {
        ArrayAdapter<Status> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Status.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_input.setAdapter(adapter);
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}
