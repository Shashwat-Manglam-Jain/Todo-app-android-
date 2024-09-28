package com.example.todoanuj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    int localLearnCount = 0;
    int localCodeCount = 0;
    int localDesignCount = 0;
    int localMeetCount = 0;
    int lll = 0;
    int ccc = 0;
    int ddd = 0;
    int mmm = 0;
    Button fab1, fab2, fab3, fab4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT); // Make status bar transparent
        window.setNavigationBarColor(Color.WHITE); // Set navigation bar color to white

        View decorView = window.getDecorView();
     decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN // Hide the status bar
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Keep the layout stable
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Optional: Hides navigation bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Optional: Hides navigation bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Makes status and navigation bars appear when swiped
        );

        fab1 = findViewById(R.id.buttonc1);
        fab2 = findViewById(R.id.buttonc2);
        fab3 = findViewById(R.id.buttonc3);
        fab4 = findViewById(R.id.buttonc4);

        View.OnClickListener onClickListener = v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            @SuppressLint("InflateParams") View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

            final CountDownLatch latch = new CountDownLatch(6); // Number of tasks

            executorService.execute(() -> {
                calculateDB();
                latch.countDown();
            });

            executorService.execute(() -> {
                calculateInternalTitle();
                latch.countDown();
            });

            executorService.execute(() -> {
                calculateExternalTitle();
                latch.countDown();
            });

            executorService.execute(() -> {
                checkExternal();
                latch.countDown();
            });

            executorService.execute(() -> {
                checkInternal();
                latch.countDown();
            });

            executorService.execute(() -> {
                checkDB();
                latch.countDown();
            });

            new Thread(() -> {
                try {
                    latch.await(); // Wait for all tasks to complete
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> updateUI(sheetView, localLearnCount, localCodeCount, localDesignCount, localMeetCount));
                runOnUiThread(() -> CheckUI(sheetView, lll, ccc, ddd, mmm));
            }).start();

            ImageButton fab = sheetView.findViewById(R.id.fab);
            fab.setOnClickListener(v1 -> {
                Intent intent = new Intent(MainActivity.this, AddTask.class);
                startActivity(intent);

                bottomSheetDialog.cancel();
                localLearnCount = 0;
                localCodeCount = 0;
               localDesignCount = 0;
                localMeetCount = 0;
                 lll = 0;
                ccc = 0;
                ddd = 0;
                mmm = 0;
            });

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
        };

        fab1.setOnClickListener(onClickListener);
        fab2.setOnClickListener(onClickListener);
        fab3.setOnClickListener(onClickListener);
        fab4.setOnClickListener(onClickListener);
    }

    private void calculateExternalTitle() {
        File externalStorageDir = getExternalFilesDir(null);

        if (externalStorageDir != null) {
            File[] files = externalStorageDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("Task_")) {
                        try (FileInputStream fis = new FileInputStream(file);
                             InputStreamReader isr = new InputStreamReader(fis);
                             BufferedReader bufferedReader = new BufferedReader(isr)) {

                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                if (line.startsWith("Title: ")) {
                                    String title = line.substring(7);
                                    switch (title) {
                                        case "Learning":
                                            localLearnCount++;
                                            break;
                                        case "Coding":
                                            localCodeCount++;
                                            break;
                                        case "Design":
                                            localDesignCount++;
                                            break;
                                        case "Meeting":
                                            localMeetCount++;
                                            break;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    private void checkExternal() {
        File externalDir = getExternalFilesDir(null); // Specific external directory
        if (externalDir != null) {
            File[] files = externalDir.listFiles((dir, name) -> name.startsWith("Task_")); // Filter files

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try (FileInputStream fis = new FileInputStream(file);
                             InputStreamReader isr = new InputStreamReader(fis);
                             BufferedReader bufferedReader = new BufferedReader(isr)) {

                            String title = null;
                            String completed = null;
                            String line;

                            while ((line = bufferedReader.readLine()) != null) {
                                if (line.startsWith("Title: ")) {
                                    title = line.substring(7);
                                } else if (line.startsWith("Completed: ")) {
                                    completed = line.substring(11);
                                }

                                if (title != null && completed != null) {
                                    synchronized (this) { // Synchronize the count updates
                                        if ("true".equals(completed)) {
                                            switch (title) {
                                                case "Learning":
                                                    lll++;
                                                    break;
                                                case "Coding":
                                                    ccc++;
                                                    break;
                                                case "Design":
                                                    ddd++;
                                                    break;
                                                case "Meeting":
                                                    mmm++;
                                                    break;
                                            }
                                        }
                                    }

                                    // Reset title and completed for the next task
                                    title = null;
                                    completed = null;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void calculateInternalTitle() {
        String[] files = fileList();

        for (String filename : files) {
            try (FileInputStream fis = openFileInput(filename);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader bufferedReader = new BufferedReader(isr)) {

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("Title: ")) {
                        String title = line.substring(7);
                        switch (title) {
                            case "Learning":
                                localLearnCount++;
                                break;
                            case "Coding":
                                localCodeCount++;
                                break;
                            case "Design":
                                localDesignCount++;
                                break;
                            case "Meeting":
                                localMeetCount++;
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkInternal() {
        String[] files = fileList();


        for (String filename : files) {
            try (FileInputStream fis = openFileInput(filename);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader bufferedReader = new BufferedReader(isr)) {

                String title = null;
                String completed = null;

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("Title: ")) {
                        title = line.substring(7); // Extract the title from the line
                    } else if (line.startsWith("Completed: ")) {
                        completed = line.substring(11); // Extract the completion status
                    }

                    if (title != null && completed != null) {
                        if ("true".equals(completed)) {
                            switch (title) {
                                case "Learning":
                                    lll++;
                                    break;
                                case "Coding":
                                    ccc++;
                                    break;
                                case "Design":
                                    ddd++;
                                    break;
                                case "Meeting":
                                    mmm++;
                                    break;
                            }
                        }

                        // Reset title and completed for the next task
                        title = null;
                        completed = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // You can log or use the counts as needed
        Log.d("INTERNAL_COUNT", "Completed Learning Tasks: " + lll);
        Log.d("INTERNAL_COUNT", "Completed Coding Tasks: " + ccc);
        Log.d("INTERNAL_COUNT", "Completed Design Tasks: " + ddd);
        Log.d("INTERNAL_COUNT", "Completed Meeting Tasks: " + mmm);
    }


    private void calculateDB() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {MyDatabaseHelper.COLUMN_TITLE};
        Cursor cursor = db.query(
                MyDatabaseHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_TITLE));
                switch (title) {
                    case "Learning":
                        localLearnCount++;
                        break;
                    case "Coding":
                        localCodeCount++;
                        break;
                    case "Design":
                        localDesignCount++;
                        break;
                    case "Meeting":
                        localMeetCount++;
                        break;
                }
            }
            cursor.close();
        }
    }

    private void checkDB() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Correct the columns array to include both COLUMN_IS_COMPLETED and COLUMN_TITLE
        String[] columns = {MyDatabaseHelper.COLUMN_IS_COMPLETED, MyDatabaseHelper.COLUMN_TITLE};
        Cursor cursor = db.query(
                MyDatabaseHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );



        if (cursor != null) {
            while (cursor.moveToNext()) {
                int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_IS_COMPLETED));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_TITLE));

                if (isCompleted == 1 && "Learning".equals(title)) {
                    lll++;
                }
                if (isCompleted == 1 && "Coding".equals(title)) {
                    ccc++;
                }
                if (isCompleted == 1 && "Design".equals(title)) {
                    ddd++;
                }
                if (isCompleted == 1 && "Meeting".equals(title)) {
                    mmm++;
                }
            }
            cursor.close(); // Close the cursor after processing all rows
        }

        // You can log the results or use the counts for further processing
        Log.d("DB_COUNT", "Completed Learning Tasks: " + lll);
        Log.d("DB_COUNT", "Completed Coding Tasks: " + ccc);
        Log.d("DB_COUNT", "Completed Design Tasks: " + ddd);
        Log.d("DB_COUNT", "Completed Meeting Tasks: " + mmm);
    }




    private void CheckUI(View sheetView, int lll, int ccc, int ddd, int mmm) {
        TextView learn = sheetView.findViewById(R.id.ll);
        if (learn != null) learn.setText(String.valueOf(lll)+" Completed");

        TextView code = sheetView.findViewById(R.id.cc);
        if (code != null) code.setText(String.valueOf(ccc)+" Completed");

        TextView design = sheetView.findViewById(R.id.dd);
        if (design != null) design.setText(String.valueOf(ddd)+" Completed");

        TextView meet = sheetView.findViewById(R.id.mm);
        if (meet != null) meet.setText(String.valueOf(mmm)+" Completed");
    }
    private void updateUI(View sheetView, int learnCount, int codeCount, int designCount, int meetCount) {
        Button learn = sheetView.findViewById(R.id.learn);
        if (learn != null) learn.setText(String.valueOf(learnCount));

        Button code = sheetView.findViewById(R.id.code);
        if (code != null) code.setText(String.valueOf(codeCount));

        Button design = sheetView.findViewById(R.id.design);
        if (design != null) design.setText(String.valueOf(designCount));

        Button meet = sheetView.findViewById(R.id.meet);
        if (meet != null) meet.setText(String.valueOf(meetCount));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service when activity is destroyed
    }
}
