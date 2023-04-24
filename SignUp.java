package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference ref = database.getReference();
    TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    FirebaseAuth mAuth;
    Button buttonReg;
    ProgressBar progressBar;
    TextView textView;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), home_page.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextConfirmPassword = findViewById(R.id.confirmPasswordEditText2);

        buttonReg = findViewById(R.id.createAccountButton);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.alreadyHaveAcc);

        progressBar.setVisibility(View.GONE);



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });



        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, cpassword;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                cpassword = String.valueOf(editTextConfirmPassword.getText());
                //name box code to database


                // Validate email
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (!password.equals(cpassword)) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Check password for at least one capital letter, one number, and minimum length of 6 characters
                if (!password.matches("^(?=.*[A-Z])(?=.*[0-9]).{6,}$")) {
                    Toast.makeText(SignUp.this, "Password must have at least one capital letter, one number, and be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignUp.this, "Account Created",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(getApplicationContext(), AddDetailsAfterSignUp.class);
                                    startActivity(intent);
                                    finish();


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUp.this, "Email taken.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

            }
        });


    }
}
