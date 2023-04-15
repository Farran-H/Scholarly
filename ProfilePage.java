package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePage extends AppCompatActivity {

    private TextView textviewFullName, textviewUserCourse, textviewUserBio, textviewUserDoB;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button btnLogOut = findViewById(R.id.btnLogOut);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        textviewFullName = findViewById(R.id.textviewFullName);
        textviewUserCourse = findViewById(R.id.textviewUserCourse);
        textviewUserBio = findViewById(R.id.textviewUserBio);
        textviewUserDoB = findViewById(R.id.textviewUserDoB);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        if (uid != null) {
            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            textviewFullName.setText(user.getFullName());
                            textviewUserCourse.setText(user.getCourse());
                            textviewUserBio.setText(user.getBio());

                            String dob = user.getDob();
                            if (dob != null && !dob.isEmpty()) {
                                int age = calculateAge(dob);
                                String ageString = String.valueOf(age) + " Years Old";
                                textviewUserDoB.setText(ageString);

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle error if needed
                }
            });
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out and start SignupActivity (Java)
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddUserDetailsPageActivity (Kotlin)
                Intent intent = new Intent(ProfilePage.this, AddUserDetailsPage.class);
                startActivity(intent);
            }
        });
    }

    // Function to calculate age from Date of Birth string
    private int calculateAge(String dobString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date dob = sdf.parse(dobString);
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);
            Calendar now = Calendar.getInstance();
            int age = now.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
