package com.example.thewatchlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thewatchlist.movie.UpdateMovie;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList movie_id, movie_name, movie_year, movie_status;
    int position;
    Activity activity;
    public CustomAdapter(Activity activity, Context context, ArrayList movie_id, ArrayList movie_name, ArrayList movie_year, ArrayList movie_status){
        this.activity=activity;
        this.context = context;
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.movie_year = movie_year;
        this.movie_status = movie_status;
    }
    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.movie_id_txt.setText(String.valueOf(movie_id.get(position)));
        holder.movie_title_txt.setText(String.valueOf(movie_name.get(position)));
        holder.movie_year_txt.setText(String.valueOf(movie_year.get(position)));
        holder.movie_status_txt.setText(String.valueOf(movie_status.get(position)));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateMovie.class);
                intent.putExtra("id", String.valueOf(movie_id.get(position)));
                intent.putExtra("title", String.valueOf(movie_name.get(position)));
                intent.putExtra("year", String.valueOf(movie_year.get(position)));
                intent.putExtra("status", String.valueOf(movie_status.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movie_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView movie_id_txt, movie_title_txt, movie_year_txt, movie_status_txt;
        LinearLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_id_txt = itemView.findViewById(R.id.movie_id_txt);
            movie_title_txt = itemView.findViewById(R.id.movie_title_txt);
            movie_year_txt = itemView.findViewById(R.id.movie_year_txt);
            movie_status_txt = itemView.findViewById(R.id.movie_status_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
