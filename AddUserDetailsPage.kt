package com.example.myapplication

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.example.myapplication.databinding.ActivityAddUserDetailsPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddUserDetailsPage : AppCompatActivity() {

    private lateinit var binding : ActivityAddUserDetailsPageBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseReference : DatabaseReference
    private lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserDetailsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance("https://scholarly-login-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")

        if (uid != null) {
            databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            binding.editfullName.setText(user.fullName)
                            binding.editUserCourse.setText(user.course)
                            binding.editUserBio.setText(user.bio)
                            binding.editUserDoB.setText(user.dob)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }





        binding.saveBtn.setOnClickListener {
            showProgressBar()

            val fullName = binding.editfullName.text.toString()
            val course = binding.editUserCourse.text.toString()
            val bio = binding.editUserBio.text.toString()
            val dob = binding.editUserDoB.text.toString()

            // Validate form fields
            if (fullName.isEmpty() || course.isEmpty() || bio.isEmpty() || dob.isEmpty()) {
                hideProgressBar()
                Toast.makeText(this@AddUserDetailsPage, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log the values of fullName, course, and bio variables
            Log.d("TAG", "fullName: $fullName")
            Log.d("TAG", "course: $course")
            Log.d("TAG", "bio: $bio")

            val user = User(fullName, course, bio, dob)
            if (uid != null) {
                databaseReference.child(uid).setValue(user).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        hideProgressBar()
                        Toast.makeText(this@AddUserDetailsPage, "Profile successfully updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ScreenAfterLogin::class.java)
                        startActivity(intent)

                    } else {
                        hideProgressBar()
                        Toast.makeText(this@AddUserDetailsPage, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    hideProgressBar()
                    Toast.makeText(this@AddUserDetailsPage, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideProgressBar()
                Toast.makeText(this@AddUserDetailsPage, "User ID is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@AddUserDetailsPage)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }
}
