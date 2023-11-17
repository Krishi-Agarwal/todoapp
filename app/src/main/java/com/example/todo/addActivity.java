package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class addActivity extends AppCompatActivity {
    public Button save;
    public TextInputLayout dateLayout;
    public TextInputLayout timeLayout;

    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        dateLayout = findViewById(R.id.date);
        timeLayout = findViewById(R.id.time);
        chipGroup = findViewById(R.id.chipGroup);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String initialDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        ((TextInputEditText) dateLayout.getEditText()).setText(initialDate);

        // Set the default time to the current time
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String initialTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
        ((TextInputEditText) timeLayout.getEditText()).setText(initialTime);
        dateLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDetailsAndStore();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String initialDate = year + "-" + (month + 1) + "-" + dayOfMonth;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        ((TextInputEditText) dateLayout.getEditText()).setText(selectedDate);
                    }
                }, year, month, dayOfMonth); // Initial date
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String initialTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        ((TextInputEditText) timeLayout.getEditText()).setText(selectedTime);
                    }
                }, hourOfDay, minute, false); // Initial time
        timePickerDialog.show();
    }

    public void fetchDetailsAndStore() {
        TextInputLayout titleLayout = findViewById(R.id.taskTitle);
        TextInputEditText titleEditText = (TextInputEditText) titleLayout.getEditText();
        TextInputEditText dateEditText = (TextInputEditText) dateLayout.getEditText();
        TextInputEditText timeEditText = (TextInputEditText) timeLayout.getEditText();
        TextInputLayout descriptionLayout = findViewById(R.id.description);
        TextInputEditText descriptionEditText = (TextInputEditText) descriptionLayout.getEditText();

        if (titleEditText != null && dateEditText != null && timeEditText != null) {
            String title = titleEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String description = descriptionEditText != null ? descriptionEditText.getText().toString() : "";

            // Get the selected chip as the category
            int selectedChipId = chipGroup.getCheckedChipId();
            String category = "";
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                category = selectedChip.getText().toString();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            try {
                Date parsedDate = new Date(dateFormat.parse(date).getTime());
                Time parsedTime = new Time(timeFormat.parse(time).getTime());

                TaskDBHelper db = new TaskDBHelper(addActivity.this);
                db.insert(title, category, parsedDate, parsedTime, description, "incomplete");

                Toast.makeText(this, "DATA INSERTED", Toast.LENGTH_SHORT).show();
                exitpage();
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date or time format.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void exitpage() {

        finish();
    }
}