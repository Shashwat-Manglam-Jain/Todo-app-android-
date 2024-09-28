package com.example.todoanuj;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import java.util.Locale;
import java.util.UUID;


public class AddTask extends AppCompatActivity {
    ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);



        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        TextView date1 = findViewById(R.id.date1);
        date1.setText(currentDate);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView date;
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddTask.this);
                View sheetView = getLayoutInflater().inflate(R.layout.writingtask, null);
                date = sheetView.findViewById(R.id.date);
                date.setText(currentDate);
                Spinner spinner = sheetView.findViewById(R.id.spinner);
                CheckBox ES = sheetView.findViewById(R.id.ES);
                CheckBox IS = sheetView.findViewById(R.id.IS);
                CheckBox DB = sheetView.findViewById(R.id.DB);
                TextView time = sheetView.findViewById(R.id.time);
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                // Set the current time in the TextView
                time.setText(currentTime);


                EditText additem = sheetView.findViewById(R.id.additem);
                Button buttonc1 = sheetView.findViewById(R.id.buttonc1);
                buttonc1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isSuccess = false;
                        String title = spinner.getSelectedItem().toString();
                        String description = additem.getText().toString();
                        String timeString = time.getText().toString();
                        String dateString = date.getText().toString();
                        if (ES.isChecked()) {
                            File externalStorage = new File(getExternalFilesDir(null), "Task" + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString());
                            try (FileOutputStream fos = new FileOutputStream(externalStorage)) {
                                fos.write(("Title: " + title + "\n").getBytes());
                                fos.write(("Time: " + timeString + "\n").getBytes());
                                fos.write(("Date: " + dateString + "\n").getBytes());
                                fos.write(("Completed: false\n").getBytes());
                                fos.write(("Description: " + description + "\n").getBytes());
                                isSuccess = true; // Mark as successful
                                readAllFilesFromExternalStorage();
                            } catch (IOException e) {
                                e.printStackTrace(); // Log the error
                            }
                        }

                        // Handle Internal Storage
                        if (IS.isChecked()) {
                            String filename = "Task" + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
                            try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
                                fos.write(("Title: " + title + "\n").getBytes());
                                fos.write(("Time: " + timeString + "\n").getBytes());
                                fos.write(("Date: " + dateString + "\n").getBytes());
                                fos.write(("Completed: false\n").getBytes());
                                fos.write(("Description: " + description + "\n").getBytes());
                                isSuccess = true; // Mark as successful
                                ReadAllFiles();
                            } catch (IOException e) {
                                e.printStackTrace(); // Log the error
                            }
                        }


                        if (DB.isChecked()) {
                            MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddTask.this);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();


                            try {
                                ContentValues values = new ContentValues();
                                values.put(MyDatabaseHelper.COLUMN_IS_COMPLETED, 0); // 0 for false
                                values.put(MyDatabaseHelper.COLUMN_DATE, dateString);
                                values.put(MyDatabaseHelper.COLUMN_TIME, timeString);
                                values.put(MyDatabaseHelper.COLUMN_TITLE, title);
                                values.put(MyDatabaseHelper.COLUMN_DESCRIPTION, description);

                                long result = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);
                                if (result != -1) {
                                    isSuccess = true; // Mark as successful
                                } else {
                                    Log.e("DB_INSERT", "Failed to insert row");
                                }
                            } catch (Exception e) {
                                Log.e("DB_INSERT_ERROR", "Error inserting data: " + e.getMessage());
                            } finally {
                                db.close(); // Ensure the database is closed
                            }

                            if (isSuccess) {

                                ReadDatabse(dateString); // Pass the required parameter
                                Toast.makeText(AddTask.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddTask.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                            }
                        }


                        // Clear fields
                        additem.setText("");
                        spinner.setSelection(0);
                        ES.setChecked(false);
                        IS.setChecked(false);
                        DB.setChecked(false);

                        // Dismiss the BottomSheetDialog
                        bottomSheetDialog.dismiss();
                    }


                });

                String[] items = {"Learning", "Coding", "Meeting", "Design"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTask.this, android.R.layout.simple_spinner_item, items);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);


                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });


        CalendarView calendarView = findViewById(R.id.calendarView3);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            private String selectedDate = "";

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Format the selected date
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                selectedDate = dateFormat.format(calendar.getTime()); // Store the selected date

                // Display the selected date in a Toast
                Toast.makeText(AddTask.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();

                // Call ReadDatabse method with the selected date
                ReadDatabse(selectedDate);
            }
        });

        checkPermissions();

        ReadDatabse(currentDate);

        ReadAllFiles();


        readAllFilesFromExternalStorage();



    }

    private void readAllFilesFromExternalStorage() {
        File externalStorageDir = getExternalFilesDir(null);

        // List all files in the directory
        File[] files = externalStorageDir.listFiles();

        // Clear any previous views
        LinearLayout taskContainer = findViewById(R.id.linearLayout333);
        taskContainer.removeAllViews();

        // Check if there are no files
        if (files == null || files.length == 0) {
            return;
        }


        // Iterate through each file and read its content
        for (File file : files) {

            String filename = file.getName();

            if (filename.startsWith("Task_")) {
                readExternalStorage(file);
            }
        }


    }

    private void readExternalStorage(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean dataExists = false;
        String title = null, date = null, time = null, description = null;
        boolean isCompleted = false;

        // Using try-with-resources to ensure resources are closed properly
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("shashwat", "external storage data:"+line);
                dataExists = true;
                // Append to stringBuilder (in case you want the full text later)
                stringBuilder.append(line).append("\n");

                // Parsing logic
                if (line.startsWith("Title: ")) {
                    title = line.substring(7);
                } else if (line.startsWith("Date: " )) {
                    date = line.substring(6);

                } else if (line.startsWith("Time: ")) {
                    time = line.substring(6);
                } else if (line.startsWith("Completed: ")) {
                    isCompleted = line.substring(11).trim().equalsIgnoreCase("true");
                } else if (line.startsWith("Description: ")) {
                    description = line.substring(13);
                }
            }

        } catch (IOException e) {
            e.printStackTrace(); // Log the error
        }

        // Inflate a new task view only if data exists
        if (dataExists) {
            View taskView = LayoutInflater.from(this).inflate(R.layout.add_db, null);

            TextView titleView = taskView.findViewById(R.id.textView4);
            CheckBox checkBox = taskView.findViewById(R.id.checkBox2);
            TextView dateView = taskView.findViewById(R.id.textView9);
            TextView descriptionView = taskView.findViewById(R.id.textView10);
            ImageView deleteIcon = taskView.findViewById(R.id.deleteIcon);

            // Populate the views with data
            titleView.setText(title != null ? title : "No Title");
            checkBox.setText(date != null ? date : "No Date");
            checkBox.setChecked(isCompleted);
            dateView.setText(time != null ? time : "No Time");
            descriptionView.setText(description != null ? description : "No Description");


            // Update CheckBox state in file
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                // Read existing content from the file
                StringBuilder fileContent = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Completed: ")) {
                            // Replace the existing Completed line with the new value
                            line = "Completed: " + isChecked;
                            if(isChecked) {
                                Toast.makeText(this, "Task Completed Successfully!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        fileContent.append(line).append("\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Write the updated content back to the file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(fileContent.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            // Set a click listener for the delete icon
            deleteIcon.setOnClickListener(v -> {
                boolean deleted = file.delete(); // Delete the file
                if (deleted) {
                    readAllFilesFromExternalStorage(); // Reload to update the view
                }
            });

            // Add the task view to the container
            LinearLayout taskContainer = findViewById(R.id.linearLayout333);
            taskContainer.addView(taskView);
        }
    }




    private void ReadAllFiles() {

        String[] files = fileList();

        LinearLayout taskContainer = findViewById(R.id.linearLayout33);
        taskContainer.removeAllViews();

        for (String filename : files) {

            if (filename.startsWith("Task_")) {

                ReadInternalStorage(filename);

            }
        }


    }


    private void ReadInternalStorage(String filename) {
        StringBuilder stringBuilder = new StringBuilder();

        // Initialize flags and variables to determine if data exists
        boolean dataExists = false;
        String title = null, date = null, time = null, description = null;
        boolean isCompleted = false;

        try (FileInputStream fis = openFileInput(filename);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                dataExists = true; // Data is present if we read any line

                // Append to stringBuilder (in case you want the full text later)
                stringBuilder.append(line).append("\n");

                // Example parsing logic: extract information from each line
                if (line.startsWith("Title: ")) {
                    title = line.substring(7); // Assume "Title: " is 7 characters long
                } else if (line.startsWith("Date: ")) {
                    date = line.substring(6);
                } else if (line.startsWith("Time: ")) {
                    time = line.substring(6);
                } else if (line.startsWith("Completed: ")) {
                    isCompleted = line.substring(11).trim().equalsIgnoreCase("true");
                } else if (line.startsWith("Description: ")) {
                    description = line.substring(13);
                }
            }

        } catch (IOException e) {
            e.printStackTrace(); // Log the error
        }

        if (dataExists) {
            LinearLayout taskContainer = findViewById(R.id.linearLayout33);

            // Inflate a new task view
            View taskView = LayoutInflater.from(this).inflate(R.layout.add_db, null);

            TextView titleView = taskView.findViewById(R.id.textView4);
            CheckBox checkBox = taskView.findViewById(R.id.checkBox2);
            TextView dateView = taskView.findViewById(R.id.textView9);
            TextView descriptionView = taskView.findViewById(R.id.textView10);
            ImageView deleteIcon = taskView.findViewById(R.id.deleteIcon); // Assuming deleteIcon is your delete button

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                // Read existing content from the file
                StringBuilder fileContent = new StringBuilder();

                try (FileInputStream fis = openFileInput(filename);
                     InputStreamReader isr = new InputStreamReader(fis);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Completed: ")) {
                            // Replace the existing Completed line with the new value
                            line = "Completed: " + isChecked;
//                            if (isChecked) {
//                                Toast.makeText(AddTask.this, "Task Completed Successfully!!", Toast.LENGTH_SHORT).show();
//                            }
                        }
                        fileContent.append(line).append("\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Write the updated content back to the file
                try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
                    fos.write(fileContent.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Populate the views with data
            titleView.setText(title != null ? title : "No Title");
            checkBox.setText(date != null ? date : "No Date");
            checkBox.setChecked(isCompleted);
            dateView.setText(time != null ? time : "No Time");
            descriptionView.setText(description != null ? description : "No Description");

            // Set a click listener for the delete icon
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean deleted = deleteFile(filename); // Delete the file

                    if (deleted) {
                        ReadAllFiles(); // Reload all files
                    }
                }
            });

            // Add the task view to the container
            taskContainer.addView(taskView);
        }
    }



    public void  ReadDatabse(String dateFilter) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddTask.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Create the selection and selectionArgs for filtering by date
            String selection = MyDatabaseHelper.COLUMN_DATE + " = ?";
            String[] selectionArgs = { dateFilter };

            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    new String[]{
                            MyDatabaseHelper.COLUMN_ID,
                            MyDatabaseHelper.COLUMN_DATE,
                            MyDatabaseHelper.COLUMN_TIME,
                            MyDatabaseHelper.COLUMN_TITLE,
                            MyDatabaseHelper.COLUMN_DESCRIPTION,
                            MyDatabaseHelper.COLUMN_IS_COMPLETED
                    },
                    selection,           // Use the selection to filter by date
                    selectionArgs,       // Provide the filter value
                    null,
                    null,
                    MyDatabaseHelper.COLUMN_ID + " DESC"
            );

            LinearLayout taskContainer = findViewById(R.id.linearLayout3);
            taskContainer.removeAllViews(); // Clear previous views



            // Process the cursor
            while (cursor.moveToNext()) {
                // Extract data from cursor
                @SuppressLint("Range") final int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DATE));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TIME));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DESCRIPTION));
                @SuppressLint("Range") int isCompleted = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_IS_COMPLETED));

                // Inflate a new task view
                View taskView = LayoutInflater.from(this).inflate(R.layout.add_db, null);

                TextView titleView = taskView.findViewById(R.id.textView4);
                CheckBox checkBox = taskView.findViewById(R.id.checkBox2);
                TextView dateView = taskView.findViewById(R.id.textView9);
                TextView descriptionView = taskView.findViewById(R.id.textView10);
                ImageView deleteIcon = taskView.findViewById(R.id.deleteIcon);

                // Populate the views with data
                titleView.setText(title);
                checkBox.setText(date);
                checkBox.setChecked(isCompleted == 1);
                dateView.setText(time);
                descriptionView.setText(description);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddTask.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        // Create the ContentValues object to store the updated values
                        ContentValues values = new ContentValues();
                        values.put(MyDatabaseHelper.COLUMN_IS_COMPLETED, isChecked ? 1 : 0); // 1 for checked, 0 for unchecked
                        if (isChecked)
                            Toast.makeText(AddTask.this, "Task Completed Successfully!!", Toast.LENGTH_SHORT).show();
                        // Define the where clause and whereArgs to update the correct row
                        String whereClause = MyDatabaseHelper.COLUMN_ID + " = ?";
                        String[] whereArgs = { String.valueOf(id) }; // Pass the ID of the task to be updated

                        // Perform the update
                        int rowsUpdated = db.update(MyDatabaseHelper.TABLE_NAME, values, whereClause, whereArgs);

                        if (rowsUpdated > 0) {
                            Log.d("DB_UPDATE", "Task updated successfully");
                        } else {
                            Log.e("DB_UPDATE", "Failed to update task");
                        }

                        db.close(); // Ensure the database is closed
                    }
                });

                // Set a click listener for the delete icon
                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddTask.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        String whereClause = MyDatabaseHelper.COLUMN_ID + " = ?";
                        String[] whereArgs = { String.valueOf(id) }; // Pass the correct ID

                        int rowsDeleted = db.delete(MyDatabaseHelper.TABLE_NAME, whereClause, whereArgs);

                        if (rowsDeleted > 0) {
                            // Refresh the displayed tasks
                            ReadDatabse(dateFilter); // Reload with the appropriate dateFilter
                        } else {
                            Log.e("DB_DELETE", "Failed to delete row");
                        }

                        db.close(); // Ensure the database is closed
                    }
                });

                // Add the task view to the container
                taskContainer.addView(taskView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // Ensure the cursor is closed
            }
            db.close(); // Ensure the database is closed
        }
    }



    private static final int REQUEST_WRITE_STORAGE = 112;

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file operations
            } else {
                // Permission denied, handle accordingly
            }
        }
    }


}