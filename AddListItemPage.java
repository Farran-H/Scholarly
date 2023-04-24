package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.ToDoListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddListItemPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private EditText editItemTitle;
    private Button pickDate;
    private Button saveBtn;
    private Button backBtn;
    private String dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tdlitem);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app/");

        editItemTitle = findViewById(R.id.editItemTitle);
        pickDate = findViewById(R.id.pickDate);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);


        // Set click listener for pickDate button
        pickDate.setOnClickListener(v -> {
            // Get the current date from Calendar
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddListItemPage.this,
                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        // Check if the selected date is in the past
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, selectedYear);
                        selectedDate.set(Calendar.MONTH, selectedMonth);
                        selectedDate.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                        if (selectedDate.before(calendar)) {
                            // Show toast message and clear the selected date
                            Toast.makeText(AddListItemPage.this, "Cannot select a date in the past", Toast.LENGTH_SHORT).show();
                            dueDate = null;
                            pickDate.setText("Pick a Date");
                        } else {
                            // Update dueDate variable with selected date
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
                            dueDate = dateFormat.format(selectedDate.getTime());

                            // Update the text of the pickDate button with the selected date
                            pickDate.setText(dueDate);
                        }
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });


        // Set click listener for Save button
        saveBtn.setOnClickListener(v -> {
            String itemTitle = editItemTitle.getText().toString().trim();

            // Check if itemTitle is not empty and dueDate is not null
            if (!itemTitle.isEmpty() && dueDate != null) {
                // Generate a unique ID for the new item
                String newItemId = UUID.randomUUID().toString();
                String userId = auth.getCurrentUser().getUid();

                // Create a new ToDoListItem object
                ToDoListItem newItem = new ToDoListItem(
                        newItemId, // Use the generated unique ID as the item ID
                        userId,
                        itemTitle,
                        dueDate, // Use the selected dueDate value
                        false // Defaulted at false
                );

                // Add the new item to the Firebase Realtime Database
                database.getReference("todo_list_items").child(newItemId).setValue(newItem)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Task was successful
                                Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_SHORT).show();

                                // Start home_page activity
                                //Intent intent = new Intent(getApplicationContext(), home_page.class);
                                Intent intent = new Intent(getApplicationContext(), ToDoList.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Task failed
                                Toast.makeText(getApplicationContext(), "Failed to add item!", Toast.LENGTH_SHORT).show();
                            }

                        });

            } else {
                Toast.makeText(getApplicationContext(), "Failed to add item!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Back button
        backBtn.setOnClickListener(v -> {
            // Finish current activity and go back
            finish();
        });
    }
}