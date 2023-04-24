package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePage extends AppCompatActivity {

    private TextView textviewFullName, textviewUserCourse, textviewUserstudentID, textviewUserDoB;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ImageView profilePicImageView;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button btnLogOut = findViewById(R.id.btnLogOut);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        //Button mapBtn = findViewById(R.id.btnMap);
        Button backBtn = findViewById(R.id.btnBack);

        // Initialize profilePicImageView
        profilePicImageView = findViewById(R.id.profilepic);

        // Initialize storageReference
        storageReference = FirebaseStorage.getInstance().getReference();

        textviewFullName = findViewById(R.id.textviewFullName);
        textviewUserCourse = findViewById(R.id.textviewUserCourse);
        textviewUserstudentID = findViewById(R.id.textviewUserstudentID);
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
                            textviewUserstudentID.setText(user.getStudentID());


                            String dob = user.getDob();
                            if (dob != null && !dob.isEmpty()) {
                                int age = calculateAge(dob);
                                String ageString = String.valueOf(age) + " Years Old";
                                textviewUserDoB.setText(ageString);
                            }

                            // Load and set profile picture
                            String uid = auth.getCurrentUser().getUid();
                            String profilePicFileName = uid + "_profilepic.jpg"; // Assuming the file name is in the format "uid_profilepic.jpg"
                            StorageReference profilePicRef = storageReference.child("profile_picture").child(profilePicFileName);
                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Load image with Picasso
                                    Picasso.get().load(uri).into(profilePicImageView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //no profile pic found
                                    profilePicImageView.setImageResource(R.drawable.defaultpic);
                                }
                            });


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
                finish();
            }
        });

        //mapBtn.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  // Start AddUserDetailsPageActivity (Kotlin)
                //Intent intent = new Intent(ProfilePage.this, MapScreen.class);
                //startActivity(intent);
            //}
        //});

        backBtn.setOnClickListener(new View.OnClickListener() {
          @Override
        public void onClick(View v) {
          // Start AddUserDetailsPageActivity (Kotlin)
        Intent intent = new Intent(ProfilePage.this, home_page.class);

        startActivity(intent);
        finish();
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


    @Override
    public void onBackPressed() {
        // Check if the current Fragment has its own back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If so, pop the back stack to go back to the previous Fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If not, redirect to the home page activity
            Intent intent = new Intent(this, home_page.class);
            startActivity(intent);
            finish(); // Optional: finish the current activity to prevent it from being kept in the back stack
        }
    }




}
