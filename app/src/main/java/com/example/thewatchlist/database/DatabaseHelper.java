package com.example.thewatchlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.thewatchlist.enums.Status;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private Context context;
    private static final String DATABASE_NAME = "TheWatchlist.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "movies";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "movie_title";
    private static final String COLUMN_YEAR = "movie_year";
    private static final String COLUMN_STATUS = "movie_status";
    private static final String COLUMN_USER_ID = "user_id";

    private static final String USER_TABLE_NAME = "users";
    private static final String USER_COLUMN_ID = "_id";
    private static final String USER_COLUMN_USERNAME = "username";
    private static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_RESET_CODE = "reset_code";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUserTableQuery = "CREATE TABLE " + USER_TABLE_NAME + " (" +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_USERNAME + " TEXT NOT NULL, " +
                USER_COLUMN_PASSWORD + " TEXT NOT NULL, " +
                USER_COLUMN_EMAIL + " TEXT NOT NULL, " +
                USER_COLUMN_RESET_CODE + " TEXT" +
                ");";
        db.execSQL(createUserTableQuery);

        String createMoviesTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_STATUS + " TEXT CHECK(" + COLUMN_STATUS + " IN ('NOT_WATCHED', 'WATCHED', 'WATCHING')), " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_COLUMN_ID + ")" +
                ");";
        db.execSQL(createMoviesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + USER_TABLE_NAME + " ADD COLUMN " + USER_COLUMN_RESET_CODE + " TEXT;");
        }
    }

    public void addMovie(String title, int year, Status status, int userId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TITLE, title);
            cv.put(COLUMN_YEAR, year);
            cv.put(COLUMN_STATUS, status.name());
            cv.put(COLUMN_USER_ID, userId);

            long result = db.insert(TABLE_NAME, null, cv);

            if (result == -1) {
                Toast.makeText(context, "Failed to add movie", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Movie added successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Cursor getAllMovies(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + "=?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public void updateMovie(String row_id, String title, String year, String status) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TITLE, title);
            cv.put(COLUMN_YEAR, year);
            cv.put(COLUMN_STATUS, status);

            long result = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{row_id});
            if (result == -1) {
                Toast.makeText(context, "Failed to update movie.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Movie updated successfully.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteMovie(String row_id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            long result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{row_id});
            if (result == -1) {
                Toast.makeText(context, "Failed to delete movie.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Movie deleted successfully.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean updateResetCode(String email, String resetCode) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(USER_COLUMN_RESET_CODE, resetCode);

            long result = db.update(USER_TABLE_NAME, cv, USER_COLUMN_EMAIL + "=?", new String[]{email});
            return result != -1;
        }
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_EMAIL + "=?";
        return db.rawQuery(query, new String[]{email});
    }
}
