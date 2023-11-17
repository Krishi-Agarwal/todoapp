package com.example.todo;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_DATE + " DATE, " +
                    COLUMN_TIME + " TIME, " +
                    COLUMN_DESCRIPTION + " TEXT, " + COLUMN_STATUS + " TEXT);";

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(String titleData, String categoryData, Date dateData, Time timeData, String descriptionData, String statusData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, titleData);
        values.put(COLUMN_CATEGORY, categoryData);
        values.put(COLUMN_DATE, dateData.toString());
        values.put(COLUMN_TIME, timeData.toString());
        values.put(COLUMN_DESCRIPTION, descriptionData);
        values.put(COLUMN_STATUS, statusData);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Task> fetchTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);

        ArrayList<Task> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Task model = new Task();
            model.id = cursor.getInt(0); // Corrected the column index
            model.title = cursor.getString(1);
            model.category = cursor.getString(2);
            model.date = Date.valueOf(cursor.getString(3));
            Log.i(TAG, "myString value: " + model.date.toString());
            model.time = Time.valueOf(cursor.getString(4));
            model.description = cursor.getString(5);
            model.status = cursor.getString(6);
            data.add(model);
        }
        db.close();
        return data;
    }
}
