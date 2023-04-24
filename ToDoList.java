package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.ToDoListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ToDoList extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    //Initialize the LinerLayout object that will be displaying the items
    private LinearLayout displayItemsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app/");

        // Reference to the LinearLayout from the XML
        displayItemsLinearLayout = findViewById(R.id.displayItemsLinearLayout);

        // Get user ID from Firebase Auth
        String userId = auth.getCurrentUser().getUid();

        // Sets up Add button from XML
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToDoList.this, AddListItemPage.class);
                startActivity(intent);
            }
        });


        // Gets to do list items from database node, sorts them in order of due date
        Query query = database.getReference("todo_list_items").orderByChild("dueDate");


        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ToDoListItem item = snapshot.getValue(ToDoListItem.class);

                    // If the user has items that match their user id, start adding each item to a linearlayout with the details
                    if (item != null && item.getUserId().equals(userId)) {
                        // Create a new LinearLayout to hold the item details
                        LinearLayout itemLayout = new LinearLayout(ToDoList.this);
                        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                        itemLayout.setBackground(ContextCompat.getDrawable(ToDoList.this, R.drawable.item_background));
                        LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                240);
                        itemLayoutParams.setMargins(20, 20, 20, 0);
                        itemLayout.setLayoutParams(itemLayoutParams);
                        itemLayout.setPadding(20, 20, 20, 20);
                        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                        itemLayout.setClipToOutline(true);

                        // Create a TextView for the item name and add it to the item layout
                        TextView itemNameTextView = new TextView(ToDoList.this);
                        itemNameTextView.setText(item.getItemName());
                        itemNameTextView.setTextColor(Color.WHITE);
                        itemNameTextView.setTextSize(20);
                        itemNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, // Set the layout height to WRAP_CONTENT
                                1.0f
                        ));
                        itemLayout.addView(itemNameTextView);

                        // Create a TextView for the due date and add it to the item layout
                        TextView dueDateTextView = new TextView(ToDoList.this);
                        dueDateTextView.setText(item.getDueDate());
                        dueDateTextView.setTextColor(Color.WHITE);
                        dueDateTextView.setGravity(Gravity.END);
                        dueDateTextView.setTextSize(20);
                        dueDateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        itemLayout.addView(dueDateTextView);

                        // Create a CheckBox to indicate if the item is done and add it to the item layout
                        CheckBox doneCheckBox = new CheckBox(ToDoList.this);
                        doneCheckBox.setChecked(item.getDone());
                        doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // Update the value of the "isDone" field in the Firebase Realtime Database for this item
                                snapshot.getRef().child("done").setValue(isChecked);
                            }
                        });

                        // Scale the size of the CheckBox
                        float scale = 1.5f; // 1.5 times larger
                        doneCheckBox.setScaleX(scale);
                        doneCheckBox.setScaleY(scale);

                        doneCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        itemLayout.addView(doneCheckBox);


                        // Create a button to delete the item and add it to the item layout
                        Button deleteButton = new Button(ToDoList.this);
                        deleteButton.setText("Del");
                        deleteButton.setBackgroundColor(Color.TRANSPARENT);
                        deleteButton.setTextColor(Color.WHITE);
                        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snapshot.getRef().removeValue();
                                displayItemsLinearLayout.removeView(itemLayout);
                                Toast.makeText(ToDoList.this, "Item Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        itemLayout.addView(deleteButton);

                        // Add the item layout to the displayItemsLinearLayout
                        displayItemsLinearLayout.addView(itemLayout);
                    }
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                // Implement your logic here
            }
        });
    }
}
