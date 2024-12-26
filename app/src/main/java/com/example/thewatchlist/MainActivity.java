package com.example.thewatchlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thewatchlist.movie.movies_page;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Animation scale_fade_in = AnimationUtils.loadAnimation(this, R.anim.scale_fade_in);

        // Apply fade-in animation to ImageView and Button
        findViewById(R.id.imageView).startAnimation(scale_fade_in);
        findViewById(R.id.getStartedButton).startAnimation(scale_fade_in);

        // Set up the button to navigate to MoviesPage
        findViewById(R.id.getStartedButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, movies_page.class);
                startActivity(intent);
            }
        });
    }
}
