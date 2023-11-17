package com.example.todo;




import static androidx.constraintlayout.widget.ConstraintSet.GONE;
import static androidx.constraintlayout.widget.ConstraintSet.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView completed;
    private ListView upcomming;
    private Button addtask;
    private RecyclerView taskRecyclerView;
    private VideoView videoView,completedVideoView;
    private CardView vcv,cvcv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date currentDate = new Date();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        String monthName = monthFormat.format(currentDate);
        String date = dateFormat.format(currentDate);
        String year = yearFormat.format(currentDate);

        TextView todaysDate = findViewById(R.id.todaysdate);
        todaysDate.setText(date + " " + monthName + " " + year);
        upcommingtasks();
        completedTasks();

        addtask = findViewById(R.id.addTask);
        addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent at = new Intent(MainActivity.this, addActivity.class);
                startActivity(at);

            }
        });
        Button refresh=findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upcommingtasks();
                completedTasks();
            }
        });
    }
    public void upcommingtasks()
    {
        vcv=(CardView)findViewById(R.id.videocardview);
        videoView = (VideoView) findViewById(R.id.video);
        vcv.setVisibility(View.GONE);

        ArrayList<Task> upcommingTasks = new ArrayList<>();
        ArrayList<String> upcommingStrings = new ArrayList<>();
        upcomming = findViewById(R.id.upcomming);
        TaskDBHelper dbh = new TaskDBHelper(this);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbh.TABLE_NAME + " WHERE status = ? order by "+dbh.COLUMN_DATE+","+dbh.COLUMN_TIME, new String[]{"incomplete"});

        while (cursor.moveToNext()) {
            Task model = new Task();
            model.id = cursor.getInt(0); // Corrected the column index
            model.title = cursor.getString(1);
            model.category = cursor.getString(2);
            model.date = java.sql.Date.valueOf(cursor.getString(3));
            model.time = Time.valueOf(cursor.getString(4));
            model.description = cursor.getString(5);
            model.status = cursor.getString(6);
            upcommingTasks.add(model);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        for (Task task : upcommingTasks) {
            upcommingStrings.add(  (dateFormat.format(task.getDate()))+ "\t\t\t\t\t\t\t" +timeFormat.format(task.getTime())+ "\n" +task.getTitle().toUpperCase() + ":\n" + task.getDescription());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, upcommingStrings);
        if (upcommingStrings.isEmpty()==false) {
            upcomming.setAdapter(adapter);

        }
        else {
//            completed.setVisibility(View.INVISIBLE);
            vcv.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.todoappvideo);

// Create a media controller and set it to the video view
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
//            videoView.setMediaController(mediaController);

// Set an OnCompletionListener to loop the video
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Restart the video when it completes
                    videoView.start();
                }
            });

            videoView.start();
            Toast.makeText(this, "NO TASK AVAILABLE", Toast.LENGTH_SHORT).show();
        }
        upcomming.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Set the dialog title and message
                builder.setTitle("MARK ITEM AS COMPLETED");
                builder.setMessage("TITLE : "+ upcommingTasks.get(position).getTitle() );

                // Set positive button and its click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase du=dbh.getWritableDatabase();
                        ContentValues cv= new ContentValues();

                        cv.put(dbh.COLUMN_STATUS,"COMPLETED");
                        String whereClause = "id=?";
                        String[] whereArgs = {String.valueOf(upcommingTasks.get(position).getId())};
                        upcommingStrings.remove(upcommingStrings.get(position));
                        int rowsUpdated = du.update(dbh.TABLE_NAME, cv, whereClause, whereArgs);
                        du.close();
                        dialog.dismiss();
                        upcommingtasks();
                        completedTasks();
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    public void completedTasks() {
        cvcv=findViewById(R.id.completedVideoCardView);
        completedVideoView=findViewById(R.id.completedVideView);
        cvcv.setVisibility(View.GONE);
        completedVideoView.setVisibility(View.GONE);
        ArrayList<Task> completedTasks = new ArrayList<>();
        ArrayList<String> completedStrings = new ArrayList<>();
        completed=findViewById(R.id.completedtasksview);

        TaskDBHelper dbh2 = new TaskDBHelper(this);
        SQLiteDatabase db2 = dbh2.getWritableDatabase();
        Cursor cursor = db2.rawQuery("SELECT * FROM " + dbh2.TABLE_NAME + " WHERE status = ?", new String[]{"COMPLETED"});

        while (cursor.moveToNext()) {
            Task model = new Task();
            model.id = cursor.getInt(0); // Corrected the column index
            model.title = cursor.getString(1);
            model.category = cursor.getString(2);
            model.date = java.sql.Date.valueOf(cursor.getString(3));
//            Log.i(TAG, "myString value: " + model.date.toString());
            model.time = Time.valueOf(cursor.getString(4));
            model.description = cursor.getString(5);
            model.status = cursor.getString(6);

            completedTasks.add(model);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        for (Task task : completedTasks) {

            completedStrings.add(  (dateFormat.format(task.getDate()))+ "\t\t\t\t\t\t\t" +timeFormat.format(task.getTime())+ "\n" +task.getTitle().toUpperCase() + ":\n" + task.getDescription());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, completedStrings);

        completed.setAdapter(adapter);

        if (completedTasks.isEmpty()) {
            cvcv.setVisibility(View.VISIBLE);
            completedVideoView.setVisibility(View.VISIBLE);
            completedVideoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.completedvideo);

// Create a media controller and set it to the video view
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
//            videoView.setMediaController(mediaController);

// Set an OnCompletionListener to loop the video
            completedVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Restart the video when it completes
                    completedVideoView.start();
                }
            });

            completedVideoView.start();
            Toast.makeText(this, "NO TASK COMPLETED", Toast.LENGTH_SHORT).show();
        }

        completed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("REMOVE TASK FROM DATABASE");
                builder.setMessage("TITLE: " + completedTasks.get(position).getTitle());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase dd = dbh2.getWritableDatabase();
                        String whereClause = "id=?";
                        String[] whereArgs = {String.valueOf(completedTasks.get(position).getId())};

                        int rowsDeleted = dd.delete(dbh2.TABLE_NAME, whereClause, whereArgs);
                        dd.close();

                        if (rowsDeleted > 0) {
                            completedTasks();
                            // Task was successfully removed from the database
                            // You can add any additional logic here.
                        }

                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

}